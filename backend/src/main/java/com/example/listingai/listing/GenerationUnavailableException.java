package com.example.listingai.listing;

public class GenerationUnavailableException extends RuntimeException {
    public GenerationUnavailableException() {
        super("Listing generation is unavailable. Configure GEMINI_API_KEY on the server.");
    }
}
