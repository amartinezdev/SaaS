package com.example.listingai.project;

import com.example.listingai.storage.ObjectStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProjectServiceTest {
    private final ProjectRepository repository = mock(ProjectRepository.class);
    private final ObjectStorage storage = mock(ObjectStorage.class);
    private final ProjectService service = new ProjectService(repository, storage, 10 * 1024 * 1024);
    private final UUID owner = UUID.randomUUID();
    private final UUID outsider = UUID.randomUUID();
    private final Project project = new Project(owner, "Lamp");

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void otherUserCannotReadOrUploadImage() {
        when(repository.findByIdAndOwnerId(project.getId(), outsider)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(outsider, project.getId()))
            .isInstanceOf(ProjectNotFoundException.class);
        assertThatThrownBy(() -> service.uploadImage(outsider, project.getId(),
            new MockMultipartFile("file", "model.png", "image/png", pngHeader())))
            .isInstanceOf(ProjectNotFoundException.class);

        verifyNoInteractions(storage);
    }

    @Test
    void rejectsWrongMimeTypeBeforeStorageCall() {
        when(repository.findByIdAndOwnerId(project.getId(), owner)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.uploadImage(owner, project.getId(),
            new MockMultipartFile("file", "model.png", "text/plain", pngHeader())))
            .isInstanceOf(InvalidImageException.class);
        verifyNoInteractions(storage);
    }

    @Test
    void rejectsFileWithForgedImageExtension() {
        when(repository.findByIdAndOwnerId(project.getId(), owner)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.uploadImage(owner, project.getId(),
            new MockMultipartFile("file", "model.png", "image/png", "not an image".getBytes())))
            .isInstanceOf(InvalidImageException.class);
        verifyNoInteractions(storage);
    }

    @Test
    void validImageIsStoredUnderProjectKey() {
        when(repository.findByIdAndOwnerId(project.getId(), owner)).thenReturn(Optional.of(project));
        when(repository.saveAndFlush(any(Project.class))).thenAnswer(call -> call.getArgument(0));
        TransactionSynchronizationManager.initSynchronization();

        Project updated = service.uploadImage(owner, project.getId(),
            new MockMultipartFile("file", "model.png", "image/png", pngHeader()));

        assertThat(updated.getImageKey()).startsWith("projects/" + project.getId() + "/source/");
        verify(storage).put(eq(updated.getImageKey()), any(byte[].class), eq("image/png"));
    }

    @Test
    void failedTransactionRemovesNewImage() {
        when(repository.findByIdAndOwnerId(project.getId(), owner)).thenReturn(Optional.of(project));
        when(repository.saveAndFlush(any(Project.class))).thenAnswer(call -> call.getArgument(0));
        TransactionSynchronizationManager.initSynchronization();
        Project updated = service.uploadImage(owner, project.getId(),
            new MockMultipartFile("file", "model.png", "image/png", pngHeader()));

        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        }

        verify(storage).delete(updated.getImageKey());
    }

    private static byte[] pngHeader() {
        return new byte[] {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
    }
}
