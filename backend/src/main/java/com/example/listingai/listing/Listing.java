package com.example.listingai.listing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "listings")
public class Listing {
    @Id private UUID id;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "generation_id", nullable = false, unique = true) private UUID generationId;
    @Column(nullable = false, length = 120) private String title;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable = false, columnDefinition = "jsonb") private List<String> tags;
    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable = false, columnDefinition = "jsonb") private List<String> keywords;
    @Column(nullable = false, length = 80) private String category;
    @Column(nullable = false, length = 10) private String language;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected Listing() {}

    public Listing(UUID projectId, UUID generationId, ListingDraft draft, String language) {
        this.id = UUID.randomUUID();
        this.projectId = projectId;
        this.generationId = generationId;
        this.title = draft.title();
        this.description = draft.description();
        this.tags = List.copyOf(draft.tags());
        this.keywords = List.copyOf(draft.keywords());
        this.category = draft.category();
        this.language = language;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getTags() { return tags; }
    public List<String> getKeywords() { return keywords; }
    public String getCategory() { return category; }
    public String getLanguage() { return language; }
    public Instant getCreatedAt() { return createdAt; }
}
