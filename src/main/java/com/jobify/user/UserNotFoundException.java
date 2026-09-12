package com.jobify.user;

public class UserNotFoundException extends RuntimeException {

    UserNotFoundException(Long userId) {
        super("User not found: " + userId);
    }

    UserNotFoundException(String email) {
        super("User not found for email: " + email);
    }
}
