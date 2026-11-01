package com.example.listingai.listing;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Component
public class ListingValidator {
    public void validate(ListingDraft draft) {
        if (!lengthBetween(draft.title(), 5, 120)
                || !lengthBetween(draft.description(), 20, 4000)
                || !lengthBetween(draft.category(), 2, 80)
                || !validTerms(draft.tags(), 3, 15, 40)
                || !validTerms(draft.keywords(), 3, 20, 60)) {
            throw new InvalidListingException();
        }
    }

    private boolean lengthBetween(String value, int min, int max) {
        return value != null && value.trim().length() >= min && value.trim().length() <= max;
    }

    private boolean validTerms(List<String> values, int min, int max, int maxLength) {
        if (values == null || values.size() < min || values.size() > max) return false;
        var unique = new HashSet<String>();
        for (String value : values) {
            if (!lengthBetween(value, 2, maxLength)
                    || !unique.add(value.trim().toLowerCase(Locale.ROOT))) return false;
        }
        return true;
    }
}
