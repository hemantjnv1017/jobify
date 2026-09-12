package com.jobify.user;

public class UserCredentialsNotFoundException extends RuntimeException {

    UserCredentialsNotFoundException(String email) {
        super("SMTP credentials not found for user: " + email);
    }
}
