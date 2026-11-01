package com.example.listingai.listing;

import com.example.listingai.credits.CreditService;
import com.example.listingai.project.Project;
import com.example.listingai.project.ProjectService;
import com.example.listingai.project.ProjectNotFoundException;
import com.example.listingai.user.User;
import com.example.listingai.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_LOCAL_INTEGRATION", matches = "true")
class ListingGenerationIntegrationTest {
    @Autowired UserService users;
    @Autowired ProjectService projects;
    @Autowired ListingGenerationService listings;
    @Autowired CreditService credits;
    @Autowired JdbcTemplate jdbc;
    @MockitoBean TextGenerationProvider provider;

    @Test
    void persistsDraftAndDebitsOnlyOnceForRepeatedRequest() {
        User user = users.findOrCreate("listing-test-" + UUID.randomUUID(), null, null);
        Project project = projectWithImage(user);
        try {
            when(provider.available()).thenReturn(true);
            when(provider.model()).thenReturn("test-model");
            when(provider.generate(any(), any(), eq("image/png"))).thenReturn(new TextGenerationProvider.GeneratedText(
                "{\"title\":\"Decorative model\",\"description\":\"A decorative object with a rounded silhouette.\","
                    + "\"tags\":[\"decor\",\"rounded\",\"model\"],"
                    + "\"keywords\":[\"3d model\",\"decor item\",\"rounded object\"],\"category\":\"Decor\"}",
                100, 50));

            Listing first = listings.generate(user.getId(), project.getId(), "request-test-1", "en");
            Listing repeated = listings.generate(user.getId(), project.getId(), "request-test-1", "en");

            assertThat(repeated.getId()).isEqualTo(first.getId());
            assertThat(listings.latest(user.getId(), project.getId()).getTitle()).isEqualTo("Decorative model");
            assertThat(credits.balance(user.getId())).isZero();
            verify(provider, times(1)).generate(any(), any(), eq("image/png"));
        } finally {
            cleanup(user, project);
        }
    }

    @Test
    void invalidProviderOutputRefundsTheCredit() {
        User user = users.findOrCreate("invalid-listing-" + UUID.randomUUID(), null, null);
        Project project = projectWithImage(user);
        try {
            when(provider.available()).thenReturn(true);
            when(provider.model()).thenReturn("test-model");
            when(provider.generate(any(), any(), eq("image/png")))
                .thenReturn(new TextGenerationProvider.GeneratedText("{}", 100, 20));

            assertThatThrownBy(() -> listings.generate(user.getId(), project.getId(),
                "request-test-2", "en")).isInstanceOf(ListingGenerationException.class);
            assertThat(credits.balance(user.getId())).isEqualTo(1);
        } finally {
            cleanup(user, project);
        }
    }

    @Test
    void anotherUserCannotGenerateFromAnOwnedProject() {
        User owner = users.findOrCreate("listing-owner-" + UUID.randomUUID(), null, null);
        User outsider = users.findOrCreate("listing-outsider-" + UUID.randomUUID(), null, null);
        Project project = projectWithImage(owner);
        try {
            when(provider.available()).thenReturn(true);
            assertThatThrownBy(() -> listings.generate(outsider.getId(), project.getId(),
                "request-other-user", "en")).isInstanceOf(ProjectNotFoundException.class);
            assertThat(credits.balance(outsider.getId())).isEqualTo(1);
            verify(provider, never()).generate(any(), any(), any());
        } finally {
            cleanup(owner, project);
            jdbc.update("DELETE FROM credit_transactions WHERE user_id = ?", outsider.getId());
            jdbc.update("DELETE FROM subscriptions WHERE user_id = ?", outsider.getId());
            jdbc.update("DELETE FROM app_users WHERE id = ?", outsider.getId());
        }
    }

    @Test
    void missingApiKeyDoesNotReserveCredit() {
        User user = users.findOrCreate("unconfigured-listing-" + UUID.randomUUID(), null, null);
        Project project = projectWithImage(user);
        try {
            when(provider.available()).thenReturn(false);
            assertThatThrownBy(() -> listings.generate(user.getId(), project.getId(),
                "request-no-api-key", "en")).isInstanceOf(GenerationUnavailableException.class);
            assertThat(credits.balance(user.getId())).isEqualTo(1);
            verify(provider, never()).generate(any(), any(), any());
        } finally {
            cleanup(user, project);
        }
    }

    private Project projectWithImage(User user) {
        Project project = projects.create(user.getId(), "Test object");
        return projects.uploadImage(user.getId(), project.getId(), new MockMultipartFile(
            "file", "object.png", "image/png",
            new byte[] {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a}));
    }

    private void cleanup(User user, Project project) {
        jdbc.update("DELETE FROM listings WHERE project_id = ?", project.getId());
        projects.delete(user.getId(), project.getId());
        jdbc.update("DELETE FROM generations WHERE user_id = ?", user.getId());
        jdbc.update("DELETE FROM credit_transactions WHERE user_id = ?", user.getId());
        jdbc.update("DELETE FROM subscriptions WHERE user_id = ?", user.getId());
        jdbc.update("DELETE FROM app_users WHERE id = ?", user.getId());
    }
}
