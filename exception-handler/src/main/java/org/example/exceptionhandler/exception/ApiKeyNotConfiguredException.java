package org.example.exceptionhandler.exception;

public class ApiKeyNotConfiguredException extends RuntimeException {
    private final String status = "500";
    private final String error = "Internal Server Error";

    public ApiKeyNotConfiguredException(String message) {
        super(message);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}