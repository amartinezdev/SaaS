package com.example.listingai.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class User {
    @Id
    private UUID id;

    @Column(name = "google_sub", nullable = false, unique = true, length = 255)
    private String googleSub;

    @Column(length = 320)
    private String email;

    @Column(length = 255)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    public User(String googleSub, String email, String name) {
        this.id = UUID.randomUUID();
        this.googleSub = googleSub;
        this.email = email;
        this.name = name;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void updateProfile(String email, String name) {
        if (!Objects.equals(this.email, email) || !Objects.equals(this.name, name)) {
            this.email = email;
            this.name = name;
            this.updatedAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public String getGoogleSub() { return googleSub; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
