package com.example.listingai.listing;

public interface TextGenerationProvider {
    boolean available();
    String model();
    GeneratedText generate(String prompt, byte[] image, String contentType);

    record GeneratedText(String json, int inputTokens, int outputTokens) {}
}
