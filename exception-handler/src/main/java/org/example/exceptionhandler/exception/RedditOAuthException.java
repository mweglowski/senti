package org.example.exceptionhandler.exception;

public class RedditOAuthException extends RuntimeException {
    private final String status = "401";
    private final String error = "Unauthorized";
    public RedditOAuthException(String message) {
        super(message);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
