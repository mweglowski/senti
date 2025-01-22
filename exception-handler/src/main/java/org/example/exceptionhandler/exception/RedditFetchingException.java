package org.example.exceptionhandler.exception;

public class RedditFetchingException extends RuntimeException {
    private final String status = "500";
    private final String error = "Internal Server Error";
    public RedditFetchingException(String message) {
        super(message);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
