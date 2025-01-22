package org.example.webapi.contract;

public class ChatBotRequest {
    private String message;
    private String context;

    private String history;

    public String getHistory() {
        return history;
    }
    public String getMessage() {
        return message;
    }

    public String getContext() {
        return context;
    }

}
