package com.example.listingai.listing;

import com.example.listingai.credits.CreditService;
import com.example.listingai.generation.Generation;
import com.example.listingai.generation.GenerationRepository;
import com.example.listingai.project.ProjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class ListingWorkflow {
    private final GenerationRepository generations;
    private final ListingRepository listings;
    private final ProjectService projects;
    private final CreditService credits;
    private final int dailyLimit;

    public ListingWorkflow(GenerationRepository generations, ListingRepository listings,
            ProjectService projects, CreditService credits,
            @Value("${app.ai.listing-daily-limit:5}") int dailyLimit) {
        if (dailyLimit < 1) throw new IllegalArgumentException("Listing daily limit must be positive");
        this.generations = generations;
        this.listings = listings;
        this.projects = projects;
        this.credits = credits;
        this.dailyLimit = dailyLimit;
    }

    @Transactional
    public Start start(UUID userId, UUID projectId, String requestKey, String model) {
        projects.get(userId, projectId);
        var existing = generations.findByUserIdAndRequestKey(userId, requestKey);
        if (existing.isPresent()) return existing(existing.get(), projectId);

        credits.debit(userId, 1, "Listing generation", "listing-debit:" + requestKey);
        existing = generations.findByUserIdAndRequestKey(userId, requestKey);
        if (existing.isPresent()) return existing(existing.get(), projectId);

        Instant todayUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        if (generations.countByUserIdAndStartedAtAfter(userId, todayUtc) >= dailyLimit) {
            throw new ListingGenerationException(HttpStatus.TOO_MANY_REQUESTS,
                "DAILY_LIMIT", "Daily listing generation limit reached");
        }
        return new Start(generations.saveAndFlush(new Generation(userId, projectId, requestKey, model)), null);
    }

    private Start existing(Generation generation, UUID projectId) {
        if (!projectId.equals(generation.getProjectId())) {
            throw new ListingGenerationException(HttpStatus.CONFLICT,
                "REQUEST_KEY_REUSED", "This request key belongs to another project");
        }
        if (generation.getStatus() == Generation.Status.COMPLETED) {
            return new Start(generation, listings.findByGenerationId(generation.getId())
                .orElseThrow(() -> new IllegalStateException("Completed listing is missing")));
        }
        throw new ListingGenerationException(HttpStatus.CONFLICT,
            "REQUEST_ALREADY_USED", "This generation request is already in progress or failed");
    }

    @Transactional
    public Listing complete(Generation generation, ListingDraft draft, String language,
                            int inputTokens, int outputTokens, BigDecimal estimatedCostUsd) {
        Generation managed = generations.findById(generation.getId())
            .orElseThrow(() -> new IllegalStateException("Generation is missing"));
        if (managed.getProjectId() == null) throw new IllegalStateException("Project was deleted during generation");
        Listing listing = listings.save(new Listing(managed.getProjectId(), managed.getId(), draft, language));
        managed.complete(inputTokens, outputTokens, estimatedCostUsd);
        return listing;
    }

    @Transactional
    public void fail(Generation generation, String code, Integer inputTokens,
                     Integer outputTokens, BigDecimal estimatedCostUsd) {
        Generation managed = generations.findById(generation.getId())
            .orElseThrow(() -> new IllegalStateException("Generation is missing"));
        managed.fail(code, inputTokens, outputTokens, estimatedCostUsd);
        credits.refund(managed.getUserId(), 1, "Failed listing generation",
            "listing-refund:" + managed.getId());
    }

    @Transactional(readOnly = true)
    public Listing latest(UUID userId, UUID projectId) {
        projects.get(userId, projectId);
        return listings.findFirstByProjectIdOrderByCreatedAtDesc(projectId)
            .orElseThrow(() -> new ListingGenerationException(HttpStatus.NOT_FOUND,
                "LISTING_NOT_FOUND", "No listing has been generated for this project"));
    }

    public record Start(Generation generation, Listing completed) {}
}
