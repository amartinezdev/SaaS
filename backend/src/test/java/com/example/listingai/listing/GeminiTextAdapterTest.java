package com.example.listingai.listing;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class GeminiTextAdapterTest {
    @Test
    void sendsImageAndStructuredSchemaAndReadsUsage() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> apiKey = new AtomicReference<>();
        server.createContext("/v1beta/models/model-test:generateContent", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            apiKey.set(exchange.getRequestHeaders().getFirst("x-goog-api-key"));
            byte[] body = ("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"title\\\":\\\"Lamp\\\"}\"}]}}],"
                + "\"usageMetadata\":{\"promptTokenCount\":100,\"totalTokenCount\":150}}").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            GeminiTextAdapter adapter = new GeminiTextAdapter("test-key", "model-test",
                "http://127.0.0.1:" + server.getAddress().getPort());
            var response = adapter.generate("Describe the image", new byte[] {1, 2, 3}, "image/png");

            assertThat(apiKey.get()).isEqualTo("test-key");
            assertThat(requestBody.get()).contains("responseSchema", "inlineData", "image/png", "AQID");
            assertThat(response.json()).contains("Lamp");
            assertThat(response.inputTokens()).isEqualTo(100);
            assertThat(response.outputTokens()).isEqualTo(50);
        } finally {
            server.stop(0);
        }
    }
}
