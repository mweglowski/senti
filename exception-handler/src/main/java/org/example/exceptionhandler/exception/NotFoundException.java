package org.example.exceptionhandler.exception;


public class NotFoundException extends RuntimeException {
    private final String status = "404";
    private final String error = "Not Found";
    public NotFoundException(String message) {
        super(message);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}