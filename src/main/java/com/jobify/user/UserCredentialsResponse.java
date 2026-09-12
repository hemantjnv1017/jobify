package com.jobify.user;

public record UserCredentialsResponse(
        Long id,
        Long userId,
        String smtpUsername
) {
    static UserCredentialsResponse from(UserCredentials credentials) {
        return new UserCredentialsResponse(
                credentials.getId(),
                credentials.getUser().getId(),
                credentials.getSmtpUsername()
        );
    }
}
