package com.cema.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserDisabledException  extends RuntimeException {

    public UserDisabledException() {
    }

    public UserDisabledException(String message) {
        super(message);
    }

    public UserDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDisabledException(Throwable cause) {
        super(cause);
    }

    public UserDisabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
