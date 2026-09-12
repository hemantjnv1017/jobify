package com.jobify.user;

import jakarta.validation.constraints.NotBlank;

public record CreateUserCredentialsRequest(
        @NotBlank(message = "SMTP username is required")
        String smtpUsername,
        @NotBlank(message = "SMTP password is required")
        String smtpPassword
) {
}
