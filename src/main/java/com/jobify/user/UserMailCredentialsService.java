package com.jobify.user;

import com.jobify.entities.User;
import com.jobify.entities.UserCredentials;
import com.jobify.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserMailCredentialsService {

    private static final Logger log = LoggerFactory.getLogger(UserMailCredentialsService.class);
    private final UserRepository userRepository;

    public UserMailCredentialsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserCredentials getCredentialsByUserEmail(final String email) {
        log.info("request email: {}", email);
        final User user = userRepository.findActiveWithCredentialsByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserCredentials credentials = user.getCredentials();

        if(credentials == null) {
            throw new IllegalArgumentException("User Credentials not found");
        }

        return credentials;
    }

//    @Transactional(readOnly = true)
//    public UserCredentials getCredentialsByUserEmail(String email) {
//        User user = userRepository.findActiveWithCredentialsByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException(email));
//        UserCredentials credentials = user.getCredentials();
//        if (credentials == null) {
//            throw new UserCredentialsNotFoundException(email);
//        }
//        return credentials;
//    }
}
