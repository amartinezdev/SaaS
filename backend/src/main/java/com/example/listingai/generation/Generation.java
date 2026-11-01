package com.example.listingai.generation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "generations")
public class Generation {
    public enum Status { PROCESSING, COMPLETED, FAILED }

    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "request_key", nullable = false, length = 120) private String requestKey;
    @Column(nullable = false, length = 30) private String type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status;
    @Column(nullable = false, length = 40) private String provider;
    @Column(nullable = false, length = 100) private String model;
    @Column(name = "prompt_version", nullable = false, length = 20) private String promptVersion;
    @Column(name = "input_tokens") private Integer inputTokens;
    @Column(name = "output_tokens") private Integer outputTokens;
    @Column(name = "estimated_cost_usd", precision = 12, scale = 8) private BigDecimal estimatedCostUsd;
    @Column(name = "credits_used", nullable = false) private int creditsUsed;
    @Column(name = "error_code", length = 60) private String errorCode;
    @Column(name = "started_at", nullable = false) private Instant startedAt;
    @Column(name = "completed_at") private Instant completedAt;

    protected Generation() {}

    public Generation(UUID userId, UUID projectId, String requestKey, String model) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.projectId = projectId;
        this.requestKey = requestKey;
        this.type = "LISTING";
        this.status = Status.PROCESSING;
        this.provider = "GEMINI";
        this.model = model;
        this.promptVersion = "v1";
        this.startedAt = Instant.now();
    }

    public void complete(int inputTokens, int outputTokens, BigDecimal estimatedCostUsd) {
        this.status = Status.COMPLETED;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.estimatedCostUsd = estimatedCostUsd;
        this.creditsUsed = 1;
        this.completedAt = Instant.now();
    }

    public void fail(String code, Integer inputTokens, Integer outputTokens, BigDecimal estimatedCostUsd) {
        this.status = Status.FAILED;
        this.errorCode = code;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.estimatedCostUsd = estimatedCostUsd;
        this.completedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getProjectId() { return projectId; }
    public Status getStatus() { return status; }
}
