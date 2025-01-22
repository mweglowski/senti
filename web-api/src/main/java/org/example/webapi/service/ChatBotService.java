package org.example.webapi.service;

import org.example.chatbotclient.ChatBotClient;
import org.example.chatbotclient.contract.ChatBotOutput;
import org.example.data.model.Comment;
import org.example.data.repository.DataCatalog;
import org.example.exceptionhandler.exception.InvalidPromptException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotService {

    private final ChatBotClient chatBotClient;
    private final DataCatalog db;
    private final LogService logService;

    public ChatBotService(ChatBotClient chatBotClient, DataCatalog db, LogService logService) {
        this.db = db;
        this.chatBotClient = chatBotClient;
        this.logService = logService;
    }

    public ChatBotOutput getResponse(String prompt) {
        if (prompt == null || prompt.isEmpty()) {
            logService.log("ERROR", "Invalid or empty prompt provided in getResponse()", "ChatBotService");
            throw new InvalidPromptException("The provided prompt is invalid or empty.");
        }

        logService.log("INFO", "Prompt provided correctly for chatbot response in getResponse()", "ChatBotService");
        return chatBotClient.getResponse(prompt);
    }

    public ChatBotOutput getPostSentimentAnalysis(Long postId) {
        List<Comment> comments = db.getComments().findAllByPostId(postId);
        logService.log("INFO", "Fetching comments for post sentiment analysis. Post ID: " + postId, "ChatBotService");

        if (comments.isEmpty()) {
            ChatBotOutput chatBotResponse = new ChatBotOutput();
            chatBotResponse.setOutput("There are no comments to analyze.");
            logService.log("INFO", "No comments found for sentiment analysis.", "ChatBotService");
            return chatBotResponse;
        }

        StringBuilder promptBuilder = new StringBuilder("Analyze the overall sentiment of these comments:\n");
        for (Comment comment : comments) {
            promptBuilder.append("- ").append(comment.getContent()).append("\n");
        }

        return chatBotClient.getResponse(promptBuilder.toString());
    }
}
