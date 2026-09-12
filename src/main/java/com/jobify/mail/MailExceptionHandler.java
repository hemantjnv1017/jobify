package com.jobify.mail;

import com.jobify.user.UserCredentialsNotFoundException;
import com.jobify.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MailExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MailExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");
        log.warn("HR mail request validation failed: {}", message);
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UserCredentialsNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCredentialsNotFound(UserCredentialsNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleMissingCv(IllegalStateException ex) {
        log.error("HR mail aborted: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "CV PDF is missing. Add it at src/main/resources/cv/Hemant_Kumar_CV.pdf"
        ));
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, String>> handleMail(MailException ex) {
        log.error("Returning 502 after HR mail failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "error", "Failed to send mail. Check SMTP username and password."
        ));
    }
}
