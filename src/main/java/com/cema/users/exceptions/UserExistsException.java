package com.cema.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistsException extends RuntimeException {

    public UserExistsException() {
    }

    public UserExistsException(String message) {
        super(message);
    }

    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserExistsException(Throwable cause) {
        super(cause);
    }

    public UserExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
