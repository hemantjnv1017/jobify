package com.jobify.user;

class DuplicateUserException extends RuntimeException {

    DuplicateUserException(String message) {
        super(message);
    }
}
