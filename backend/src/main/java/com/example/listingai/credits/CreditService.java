package com.example.listingai.credits;

import com.example.listingai.credits.CreditTransaction.Type;
import com.example.listingai.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreditService {
    public static class InsufficientCreditsException extends IllegalStateException {
        public InsufficientCreditsException() { super("Insufficient credits"); }
    }
    private final UserRepository users;
    private final CreditTransactionRepository transactions;
    private final long initialFreeCredits;

    public CreditService(UserRepository users, CreditTransactionRepository transactions,
                         @Value("${app.credits.initial-free:1}") long initialFreeCredits) {
        if (initialFreeCredits < 0) throw new IllegalArgumentException("Initial credits cannot be negative");
        this.users = users;
        this.transactions = transactions;
        this.initialFreeCredits = initialFreeCredits;
    }

    @Transactional
    public void grantInitial(UUID userId) {
        if (initialFreeCredits > 0) {
            grant(userId, initialFreeCredits, "Initial free credit", "welcome:" + userId);
        }
    }

    @Transactional
    public CreditTransaction grant(UUID userId, long amount, String reason, String idempotencyKey) {
        if (amount <= 0) throw new IllegalArgumentException("Grant amount must be positive");
        return record(userId, Type.GRANT, amount, reason, idempotencyKey);
    }

    @Transactional
    public CreditTransaction debit(UUID userId, long amount, String reason, String idempotencyKey) {
        if (amount <= 0) throw new IllegalArgumentException("Debit amount must be positive");
        return record(userId, Type.DEBIT, Math.negateExact(amount), reason, idempotencyKey);
    }

    @Transactional
    public CreditTransaction refund(UUID userId, long amount, String reason, String idempotencyKey) {
        if (amount <= 0) throw new IllegalArgumentException("Refund amount must be positive");
        return record(userId, Type.REFUND, amount, reason, idempotencyKey);
    }

    @Transactional
    public CreditTransaction adjust(UUID userId, long amount, String reason, String idempotencyKey) {
        if (amount == 0) throw new IllegalArgumentException("Adjustment cannot be zero");
        return record(userId, Type.ADJUSTMENT, amount, reason, idempotencyKey);
    }

    @Transactional(readOnly = true)
    public long balance(UUID userId) {
        return transactions.balance(userId);
    }

    private CreditTransaction record(UUID userId, Type type, long amount, String reason, String idempotencyKey) {
        if (reason == null || reason.isBlank() || reason.length() > 255
                || idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 255) {
            throw new IllegalArgumentException("Reason and idempotency key are required and must fit the ledger");
        }

        // Serialize changes per user so simultaneous debits cannot both spend the same balance.
        users.lockById(userId).orElseThrow(() -> new IllegalArgumentException("Unknown user"));
        var existing = transactions.findByUserIdAndIdempotencyKey(userId, idempotencyKey);
        if (existing.isPresent()) {
            CreditTransaction transaction = existing.get();
            if (transaction.getType() != type || transaction.getAmount() != amount
                    || !transaction.getReason().equals(reason)) {
                throw new IllegalStateException("Idempotency key was used for a different credit operation");
            }
            return transaction;
        }

        long nextBalance = Math.addExact(transactions.balance(userId), amount);
        if (nextBalance < 0) throw new InsufficientCreditsException();
        return transactions.save(new CreditTransaction(userId, type, amount, reason, idempotencyKey));
    }
}
