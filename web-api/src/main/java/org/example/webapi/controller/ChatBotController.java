package org.example.webapi.controller;

import org.example.chatbotclient.contract.ChatBotOutput;
import org.example.webapi.contract.ChatBotRequest;
import org.example.webapi.service.ChatBotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {
    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping
    public ResponseEntity<ChatBotOutput> getResponse(@RequestBody ChatBotRequest requestBody) {
        String message = requestBody.getMessage();
        String context = requestBody.getContext();
        String history = requestBody.getHistory();

        String prompt = "";

        if (!context.isEmpty()) {
            prompt += " Context: \"" + context + "\".";
        }
        if (!history.isEmpty()) {
            prompt += " Conversation History: \"" + history + "\".";
        }

        prompt += "Now please answer the following message: \"" + message + "\"";
        System.out.println(prompt);

        ChatBotOutput chatBotResponse = chatBotService.getResponse(prompt);

        return new ResponseEntity<>(chatBotResponse, HttpStatus.OK);
    }

    @GetMapping("/analyze-sentiment/{postId}")
    public ResponseEntity<ChatBotOutput> getPostSentimentAnalysis(@PathVariable Long postId) {
        ChatBotOutput chatBotResponse = chatBotService.getPostSentimentAnalysis(postId);

        return new ResponseEntity<>(chatBotResponse, HttpStatus.OK);
    }
}