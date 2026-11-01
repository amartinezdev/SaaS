package com.example.listingai.listing;

import java.util.List;

public record ListingDraft(String title, String description, List<String> tags,
                           List<String> keywords, String category) {}
