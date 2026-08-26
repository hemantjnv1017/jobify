package com.jobify.mail;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;

public record SendHrMailRequest(
        @NotBlank(message = "HR email is required")
        @Email(message = "HR email must be valid")
        String email,
        @NotBlank(message = "HR name is required")
        @JsonAlias({"Hrname", "hrname"})
        String hrName,
        @NotBlank(message = "Role is required")
        @JsonAlias({"Role"})
        String role,
        ArrayList<@Email(message = "CC email must be valid") String> cc
) {
    public SendHrMailRequest {
        cc = cc == null ? new ArrayList<>() : new ArrayList<>(cc);
    }
}
