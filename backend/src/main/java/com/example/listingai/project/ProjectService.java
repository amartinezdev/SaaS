package com.example.listingai.project;

import com.example.listingai.storage.ObjectStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projects;
    private final ObjectStorage storage;
    private final long maxImageBytes;

    public ProjectService(ProjectRepository projects, ObjectStorage storage,
                          @Value("${app.uploads.max-image-bytes}") long maxImageBytes) {
        this.projects = projects;
        this.storage = storage;
        this.maxImageBytes = maxImageBytes;
    }

    @Transactional
    public Project create(UUID ownerId, String name) {
        return projects.save(new Project(ownerId, name.trim()));
    }

    @Transactional(readOnly = true)
    public List<Project> list(UUID ownerId) {
        return projects.findAllByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    @Transactional(readOnly = true)
    public Project get(UUID ownerId, UUID id) {
        return ownedProject(ownerId, id);
    }

    @Transactional
    public Project rename(UUID ownerId, UUID id, String name) {
        Project project = ownedProject(ownerId, id);
        project.rename(name.trim());
        return project;
    }

    @Transactional
    public void delete(UUID ownerId, UUID id) {
        Project project = ownedProject(ownerId, id);
        String imageKey = project.getImageKey();
        projects.delete(project);
        deleteAfterCommit(imageKey);
    }

    @Transactional
    public Project uploadImage(UUID ownerId, UUID id, MultipartFile file) {
        Project project = ownedProject(ownerId, id);
        ValidatedImage image = validate(file);
        String newKey = "projects/" + id + "/source/" + UUID.randomUUID() + image.extension();
        storage.put(newKey, image.bytes(), image.contentType());
        deleteAfterRollback(newKey);
        String oldKey = project.getImageKey();
        project.setImage(newKey, image.contentType());
        projects.saveAndFlush(project);
        deleteAfterCommit(oldKey);
        return project;
    }

    public ProjectImage image(UUID ownerId, UUID id) {
        Project project = ownedProject(ownerId, id);
        if (project.getImageKey() == null) {
            throw new ProjectNotFoundException();
        }
        return new ProjectImage(storage.get(project.getImageKey()), project.getImageContentType());
    }

    private Project ownedProject(UUID ownerId, UUID id) {
        return projects.findByIdAndOwnerId(id, ownerId).orElseThrow(ProjectNotFoundException::new);
    }

    private void deleteAfterCommit(String key) {
        if (key == null) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    storage.delete(key);
                } catch (RuntimeException error) {
                    log.warn("Could not remove obsolete project image {}", key, error);
                }
            }
        });
    }

    private void deleteAfterRollback(String key) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    try {
                        storage.delete(key);
                    } catch (RuntimeException error) {
                        log.warn("Could not remove rolled-back project image {}", key, error);
                    }
                }
            }
        });
    }

    private ValidatedImage validate(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > maxImageBytes) {
            throw new InvalidImageException("Image must be between 1 byte and 10 MB");
        }
        String name = file.getOriginalFilename();
        String extension = name == null || !name.contains(".") ? ""
            : name.substring(name.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        String type = file.getContentType();
        String expectedType = switch (extension) {
            case ".png" -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".webp" -> "image/webp";
            default -> throw new InvalidImageException("Use a PNG, JPEG or WebP image");
        };
        if (!expectedType.equals(type)) {
            throw new InvalidImageException("Image extension and MIME type do not match");
        }
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length > maxImageBytes || !matchesSignature(bytes, expectedType)) {
                throw new InvalidImageException("Image content is invalid");
            }
            return new ValidatedImage(bytes, expectedType, extension);
        } catch (IOException error) {
            throw new InvalidImageException("Image could not be read");
        }
    }

    private boolean matchesSignature(byte[] bytes, String type) {
        return switch (type) {
            case "image/png" -> bytes.length >= 8 && Arrays.equals(
                Arrays.copyOf(bytes, 8), new byte[] {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a});
            case "image/jpeg" -> bytes.length >= 3 && (bytes[0] & 0xff) == 0xff
                && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff;
            case "image/webp" -> bytes.length >= 12 && new String(bytes, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
                && new String(bytes, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP");
            default -> false;
        };
    }

    private record ValidatedImage(byte[] bytes, String contentType, String extension) {}
    public record ProjectImage(byte[] bytes, String contentType) {}
}
