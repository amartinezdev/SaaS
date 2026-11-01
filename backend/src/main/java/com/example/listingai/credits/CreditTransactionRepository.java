package com.example.listingai.credits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, UUID> {
    Optional<CreditTransaction> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);

    @Query("select coalesce(sum(t.amount), 0) from CreditTransaction t where t.userId = :userId")
    long balance(@Param("userId") UUID userId);
}
