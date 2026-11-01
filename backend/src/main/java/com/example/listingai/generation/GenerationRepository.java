package com.example.listingai.generation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

public interface GenerationRepository extends JpaRepository<Generation, UUID> {
    Optional<Generation> findByUserIdAndRequestKey(UUID userId, String requestKey);
    long countByUserIdAndStartedAtAfter(UUID userId, Instant instant);
}
