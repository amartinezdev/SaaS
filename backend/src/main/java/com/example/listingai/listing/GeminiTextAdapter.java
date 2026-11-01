package com.example.listingai.listing;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class GeminiTextAdapter implements TextGenerationProvider {
    private final RestClient client;
    private final String apiKey;
    private final String model;

    public GeminiTextAdapter(
            @Value("${app.ai.gemini-api-key:}") String apiKey,
            @Value("${app.ai.text-model:gemini-3.5-flash-lite}") String model,
            @Value("${app.ai.gemini-base-url:https://generativelanguage.googleapis.com}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(45_000);
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }

    @Override public boolean available() { return apiKey != null && !apiKey.isBlank(); }
    @Override public String model() { return model; }

    @Override
    public GeneratedText generate(String prompt, byte[] image, String contentType) {
        if (!available()) throw new GenerationUnavailableException();
        if (!model.matches("[a-zA-Z0-9._-]+")) throw new ProviderGenerationException("INVALID_MODEL");

        Map<String, Object> schema = Map.of(
            "type", "OBJECT",
            "properties", Map.of(
                "title", Map.of("type", "STRING"),
                "description", Map.of("type", "STRING"),
                "tags", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
                "keywords", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
                "category", Map.of("type", "STRING")
            ),
            "required", List.of("title", "description", "tags", "keywords", "category")
        );
        Map<String, Object> request = Map.of(
            "contents", List.of(Map.of("role", "user", "parts", List.of(
                Map.of("text", prompt),
                Map.of("inlineData", Map.of("mimeType", contentType,
                    "data", Base64.getEncoder().encodeToString(image)))
            ))),
            "generationConfig", Map.of("responseMimeType", "application/json",
                "responseSchema", schema, "maxOutputTokens", 2048)
        );

        try {
            JsonNode response = client.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 429,
                    (sent, received) -> { throw new ProviderGenerationException("RATE_LIMITED"); })
                .onStatus(status -> status.isError(),
                    (sent, received) -> { throw new ProviderGenerationException("PROVIDER_ERROR"); })
                .body(JsonNode.class);
            if (response == null) throw new ProviderGenerationException("EMPTY_RESPONSE");
            JsonNode text = response.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (!text.isTextual() || text.asText().isBlank()) {
                throw new ProviderGenerationException("EMPTY_RESPONSE");
            }
            JsonNode usage = response.path("usageMetadata");
            int input = usage.path("promptTokenCount").asInt(0);
            int total = usage.path("totalTokenCount").asInt(0);
            int output = Math.max(usage.path("candidatesTokenCount").asInt(0), total - input);
            return new GeneratedText(text.asText(), input, output);
        } catch (ProviderGenerationException error) {
            throw error;
        } catch (RestClientException error) {
            throw new ProviderGenerationException("PROVIDER_UNAVAILABLE");
        }
    }
}
