package org.example.chatbotclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chatbotclient.config.ChatBotClientConfig;
import org.example.chatbotclient.contract.ChatBotOutput;
import org.example.exceptionhandler.exception.ChatBotApiException;
import org.example.exceptionhandler.exception.InvalidPromptException;
import org.example.exceptionhandler.exception.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatBotClient {
    private final RestTemplate restClient;
    private final ObjectMapper objectMapper;
    private final ChatBotClientConfig config;


    public ChatBotClient(ChatBotClientConfig config, ObjectMapper objectMapper) {
        this.restClient = new RestTemplate();
        this.objectMapper = objectMapper;
        this.config = config;
    }

    public ChatBotOutput getResponse(String prompt) {
        if (prompt == null || prompt.isEmpty()) {
//            logger.error("Invalid or empty prompt provided.");
            throw new InvalidPromptException("The provided prompt is invalid or empty.");
        }

        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
//            logger.error("API key not found in the configuration.");
            throw new NotFoundException("API key not found in the configuration.");
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + config.getApiKey());

            HttpEntity<String> httpEntity = new HttpEntity<>(jsonRequestBody, headers);

            ResponseEntity<String> responseEntity = restClient.postForEntity(config.getApiUrl(), httpEntity, String.class);

            Map<String, Object> jsonResponse = objectMapper.readValue(responseEntity.getBody(), Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) jsonResponse.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String chatbotResponseContent = (String) message.get("content");

            ChatBotOutput chatbotResponse = new ChatBotOutput();
            chatbotResponse.setOutput(chatbotResponseContent);

//            logger.info("Chatbot response successfully retrieved.");
            return chatbotResponse;

        } catch (Exception e) {
//            logger.error("Error while calling the ChatBot API.", e);
            throw new ChatBotApiException("An error occurred while calling the ChatBot API. Error: " + e.getMessage());
        }
    }
}
