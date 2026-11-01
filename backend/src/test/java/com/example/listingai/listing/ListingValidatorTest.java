package com.example.listingai.listing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListingValidatorTest {
    private final ListingValidator validator = new ListingValidator();

    @Test
    void rejectsDuplicateTagsIgnoringCase() {
        assertThatThrownBy(() -> validator.validate(new ListingDraft(
            "Decorative model", "A decorative object with a rounded silhouette.",
            List.of("Decor", "decor", "model"),
            List.of("3d model", "decor item", "rounded object"), "Decor")))
            .isInstanceOf(InvalidListingException.class);
    }

    @Test
    void rejectsMissingDescription() {
        assertThatThrownBy(() -> validator.validate(new ListingDraft(
            "Decorative model", "short", List.of("decor", "rounded", "model"),
            List.of("3d model", "decor item", "rounded object"), "Decor")))
            .isInstanceOf(InvalidListingException.class);
    }
}
