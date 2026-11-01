package com.example.listingai.listing;

import com.example.listingai.auth.GoogleOidcUserService;
import com.example.listingai.auth.SecurityConfig;
import com.example.listingai.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListingController.class)
@Import(SecurityConfig.class)
class ListingControllerSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean ListingGenerationService listings;
    @MockitoBean UserService users;
    @MockitoBean GoogleOidcUserService googleUsers;
    @MockitoBean ClientRegistrationRepository registrations;

    @Test
    void anonymousCannotReadListing() throws Exception {
        mvc.perform(get("/api/projects/{id}/listing", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void generationRequiresCsrf() throws Exception {
        mvc.perform(post("/api/projects/{id}/listing/generate", UUID.randomUUID())
                .with(oidcLogin()).header("Idempotency-Key", "request-test-1")
                .contentType(MediaType.APPLICATION_JSON).content("{\"language\":\"en\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void generationRequiresIdempotencyKey() throws Exception {
        mvc.perform(post("/api/projects/{id}/listing/generate", UUID.randomUUID())
                .with(oidcLogin()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"language\":\"en\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
