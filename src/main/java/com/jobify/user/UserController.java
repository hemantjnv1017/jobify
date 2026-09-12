package com.jobify.user;

import com.jobify.entities.User;
import com.jobify.entities.UserCredentials;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Received create user request email={}", request.email());
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/{userId}/credentials")
    public ResponseEntity<UserCredentialsResponse> addCredentials(
            @PathVariable Long userId,
            @Valid @RequestBody CreateUserCredentialsRequest request
    ) {
        log.info("Received store credentials request for user id={}", userId);
        UserCredentials credentials = userService.addCredentials(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserCredentialsResponse.from(credentials));
    }
}
