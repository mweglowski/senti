package org.example.exceptionhandler.exception;

public class ChatBotApiException extends RuntimeException {
    private final String status = "500";
    private final String error = "Internal Server Error";
    public ChatBotApiException(String message) {
        super(message);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}