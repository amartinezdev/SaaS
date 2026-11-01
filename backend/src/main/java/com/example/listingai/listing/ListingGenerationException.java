package com.example.listingai.listing;

import org.springframework.http.HttpStatus;

public class ListingGenerationException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ListingGenerationException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() { return status; }
    public String code() { return code; }
}
