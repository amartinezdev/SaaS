package com.example.listingai.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import software.amazon.awssdk.core.exception.SdkException;

@RestControllerAdvice(assignableTypes = ProjectController.class)
public class ProjectErrorHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    ResponseEntity<ApiError> notFound() {
        return error(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "Project not found");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class,
        MissingServletRequestPartException.class})
    ResponseEntity<ApiError> invalidRequest() {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Invalid project request");
    }

    @ExceptionHandler(InvalidImageException.class)
    ResponseEntity<ApiError> invalidImage(InvalidImageException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_IMAGE", exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiError> tooLarge() {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "IMAGE_TOO_LARGE", "Image exceeds the 10 MB limit");
    }

    @ExceptionHandler(SdkException.class)
    ResponseEntity<ApiError> storageUnavailable() {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "STORAGE_UNAVAILABLE", "Image storage is unavailable");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(code, message));
    }

    public record ApiError(String code, String message) {}
}
