package com.jobify.user;

import com.jobify.entities.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        LocalDateTime createdTime,
        LocalDateTime updatedTime,
        boolean deleted
) {
    static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedTime(),
                user.getUpdatedTime(),
                user.isDeleted()
        );
    }
}
