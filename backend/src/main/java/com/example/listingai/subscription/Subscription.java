package com.example.listingai.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_code", nullable = false)
    private Plan plan;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Subscription() {}

    public Subscription(UUID userId, Plan plan) {
        this.userId = userId;
        this.plan = plan;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public UUID getUserId() { return userId; }
    public Plan getPlan() { return plan; }
}
