package com.example.listingai.listing;

public class ProviderGenerationException extends RuntimeException {
    private final String code;

    public ProviderGenerationException(String code) {
        super("The AI provider could not generate a listing");
        this.code = code;
    }

    public String code() { return code; }
}
