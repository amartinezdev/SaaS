package com.example.listingai.listing;

public class InvalidListingException extends RuntimeException {
    public InvalidListingException() {
        super("The generated listing did not pass validation");
    }
}
