package com.example.listingai.project;

import com.example.listingai.storage.ObjectStorage;
import com.example.listingai.user.User;
import com.example.listingai.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.exception.SdkException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_LOCAL_INTEGRATION", matches = "true")
class ProjectStorageIntegrationTest {
    @Autowired private UserRepository users;
    @Autowired private ProjectService projects;
    @Autowired private ObjectStorage storage;

    @Test
    void imageRoundTripAndOwnershipWithPostgresAndS3() {
        User owner = users.save(new User("integration-" + UUID.randomUUID(), null, "Integration test"));
        Project project = projects.create(owner.getId(), "Test model");
        try {
            byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
            Project uploaded = projects.uploadImage(owner.getId(), project.getId(),
                new MockMultipartFile("file", "model.png", "image/png", png));

            assertThat(uploaded.getImageKey()).isNotNull();
            assertThat(projects.image(owner.getId(), project.getId()).bytes()).isEqualTo(png);
            assertThatThrownBy(() -> projects.image(UUID.randomUUID(), project.getId()))
                .isInstanceOf(ProjectNotFoundException.class);

            String key = uploaded.getImageKey();
            projects.delete(owner.getId(), project.getId());
            assertThatThrownBy(() -> storage.get(key)).isInstanceOf(SdkException.class);
        } finally {
            if (projects.list(owner.getId()).stream().anyMatch(p -> p.getId().equals(project.getId()))) {
                projects.delete(owner.getId(), project.getId());
            }
            users.deleteById(owner.getId());
        }
    }
}
