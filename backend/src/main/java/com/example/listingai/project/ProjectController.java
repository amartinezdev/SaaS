package com.example.listingai.project;

import com.example.listingai.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projects;
    private final UserService users;

    public ProjectController(ProjectService projects, UserService users) {
        this.projects = projects;
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@AuthenticationPrincipal OidcUser principal,
            @Valid @RequestBody ProjectNameRequest request) {
        Project project = projects.create(ownerId(principal), request.name());
        return ResponseEntity.created(URI.create("/api/projects/" + project.getId()))
            .body(ProjectResponse.from(project));
    }

    @GetMapping
    public List<ProjectResponse> list(@AuthenticationPrincipal OidcUser principal) {
        return projects.list(ownerId(principal)).stream().map(ProjectResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@AuthenticationPrincipal OidcUser principal, @PathVariable UUID id) {
        return ProjectResponse.from(projects.get(ownerId(principal), id));
    }

    @PutMapping("/{id}")
    public ProjectResponse rename(@AuthenticationPrincipal OidcUser principal, @PathVariable UUID id,
            @Valid @RequestBody ProjectNameRequest request) {
        return ProjectResponse.from(projects.rename(ownerId(principal), id, request.name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal OidcUser principal, @PathVariable UUID id) {
        projects.delete(ownerId(principal), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProjectResponse uploadImage(@AuthenticationPrincipal OidcUser principal, @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ProjectResponse.from(projects.uploadImage(ownerId(principal), id, file));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@AuthenticationPrincipal OidcUser principal, @PathVariable UUID id) {
        ProjectService.ProjectImage image = projects.image(ownerId(principal), id);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .contentType(MediaType.parseMediaType(image.contentType()))
            .body(image.bytes());
    }

    private UUID ownerId(OidcUser principal) {
        return users.getByGoogleSub(principal.getSubject()).getId();
    }

    public record ProjectNameRequest(@NotBlank @Size(max = 160) String name) {}

    public record ProjectResponse(UUID id, String name, boolean hasImage, Instant createdAt, Instant updatedAt) {
        static ProjectResponse from(Project project) {
            return new ProjectResponse(project.getId(), project.getName(),
                project.getImageKey() != null, project.getCreatedAt(), project.getUpdatedAt());
        }
    }
}
