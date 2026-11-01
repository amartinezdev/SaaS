package com.example.listingai.listing;

import com.example.listingai.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/listing")
public class ListingController {
    private final UserService users;
    private final ListingGenerationService listings;

    public ListingController(UserService users, ListingGenerationService listings) {
        this.users = users;
        this.listings = listings;
    }

    @PostMapping("/generate")
    public ListingResponse generate(@AuthenticationPrincipal OidcUser principal,
            @PathVariable UUID projectId, @RequestHeader("Idempotency-Key") String requestKey,
            @Valid @RequestBody GenerateRequest request) {
        UUID userId = users.getByGoogleSub(principal.getSubject()).getId();
        return ListingResponse.from(listings.generate(userId, projectId, requestKey, request.language()));
    }

    @GetMapping
    public ListingResponse latest(@AuthenticationPrincipal OidcUser principal,
            @PathVariable UUID projectId) {
        UUID userId = users.getByGoogleSub(principal.getSubject()).getId();
        return ListingResponse.from(listings.latest(userId, projectId));
    }

    public record GenerateRequest(@Pattern(regexp = "en|es") String language) {}

    public record ListingResponse(UUID id, String title, String description, List<String> tags,
                                  List<String> keywords, String category, String language, Instant createdAt) {
        static ListingResponse from(Listing listing) {
            return new ListingResponse(listing.getId(), listing.getTitle(), listing.getDescription(),
                listing.getTags(), listing.getKeywords(), listing.getCategory(), listing.getLanguage(),
                listing.getCreatedAt());
        }
    }
}
