package com.example.listingai.listing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    Optional<Listing> findFirstByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<Listing> findByGenerationId(UUID generationId);
}
