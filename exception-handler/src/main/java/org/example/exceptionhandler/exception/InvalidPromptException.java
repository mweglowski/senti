package org.example.exceptionhandler.exception;

public class InvalidPromptException extends RuntimeException {
    private final String status = "400";
    private final String error = "Bad Request";
    public InvalidPromptException(String message) {
        super(message);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}