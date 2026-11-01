package com.example.listingai.listing;

import com.example.listingai.credits.CreditService;
import com.example.listingai.project.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ListingController.class)
public class ListingErrorHandler {
    @ExceptionHandler(ListingGenerationException.class)
    ResponseEntity<ApiError> generation(ListingGenerationException error) {
        return ResponseEntity.status(error.status()).body(new ApiError(error.code(), error.getMessage()));
    }

    @ExceptionHandler(GenerationUnavailableException.class)
    ResponseEntity<ApiError> unavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ApiError("GENERATION_UNAVAILABLE", "Listing generation is not configured"));
    }

    @ExceptionHandler(CreditService.InsufficientCreditsException.class)
    ResponseEntity<ApiError> insufficientCredits() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("INSUFFICIENT_CREDITS", "No credits remain for listing generation"));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    ResponseEntity<ApiError> projectNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("PROJECT_NOT_FOUND", "Project not found"));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingRequestHeaderException.class})
    ResponseEntity<ApiError> invalidRequest() {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", "Invalid listing request"));
    }

    public record ApiError(String code, String message) {}
}
