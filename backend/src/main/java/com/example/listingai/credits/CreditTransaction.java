package com.example.listingai.credits;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit_transactions")
public class CreditTransaction {
    public enum Type { GRANT, DEBIT, REFUND, ADJUSTMENT }

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "idempotency_key", nullable = false, length = 255)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CreditTransaction() {}

    public CreditTransaction(UUID userId, Type type, long amount, String reason, String idempotencyKey) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.reason = reason;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = Instant.now();
    }

    public Type getType() { return type; }
    public long getAmount() { return amount; }
    public String getReason() { return reason; }
}
