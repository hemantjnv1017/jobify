package com.jobify.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;

    public UserService(
            UserRepository userRepository,
            UserCredentialsRepository userCredentialsRepository
    ) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.email());
        User saved = userRepository.save(user);
        log.info("Created user id={}", saved.getId());
        return saved;
    }

    @Transactional
    public UserCredentials addCredentials(Long userId, CreateUserCredentialsRequest request) {
        User user = userRepository.findById(userId)
                .filter(existing -> !existing.isDeleted())
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (userCredentialsRepository.existsByUser_Id(userId)) {
            throw new DuplicateUserException("Credentials already exist for this user");
        }
        UserCredentials credentials = new UserCredentials();
        credentials.setSmtpUsername(request.smtpUsername());
        credentials.setSmtpPassword(request.smtpPassword());
        user.setCredentials(credentials);
        UserCredentials saved = userCredentialsRepository.save(credentials);
        log.info("Stored SMTP credentials id={} for user id={}", saved.getId(), userId);
        return saved;
    }
}
