package com.example.listingai.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "image_key", length = 512)
    private String imageKey;

    @Column(name = "image_content_type", length = 64)
    private String imageContentType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Project() {}

    public Project(UUID ownerId, String name) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.name = name;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void rename(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void setImage(String key, String contentType) {
        this.imageKey = key;
        this.imageContentType = contentType;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getImageKey() { return imageKey; }
    public String getImageContentType() { return imageContentType; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
