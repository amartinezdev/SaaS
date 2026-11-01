package com.example.listingai.listing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ListingCostEstimator {
    private static final BigDecimal MILLION = BigDecimal.valueOf(1_000_000);
    private final BigDecimal inputPerMillion;
    private final BigDecimal outputPerMillion;

    public ListingCostEstimator(@Value("${app.ai.text-input-usd-per-million:0.30}") BigDecimal inputPerMillion,
                                @Value("${app.ai.text-output-usd-per-million:2.50}") BigDecimal outputPerMillion) {
        this.inputPerMillion = inputPerMillion;
        this.outputPerMillion = outputPerMillion;
    }

    public BigDecimal estimate(int inputTokens, int outputTokens) {
        return inputPerMillion.multiply(BigDecimal.valueOf(inputTokens))
            .add(outputPerMillion.multiply(BigDecimal.valueOf(outputTokens)))
            .divide(MILLION, 8, RoundingMode.HALF_UP);
    }
}
