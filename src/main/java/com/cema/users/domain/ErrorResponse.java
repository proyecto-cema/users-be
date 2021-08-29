package com.cema.users.domain;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private String message;
    private String details;
    private final List<Violation> violations = new ArrayList<>();

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse() {
    }

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public static class Violation {

        private final String fieldName;

        private final String message;

        public Violation(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getMessage() {
            return message;
        }
    }
}
