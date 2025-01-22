package org.example.exceptionhandler;

import org.example.exceptionhandler.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, String>> buildResponse(Exception e, String status, String error, HttpStatus httpStatus) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("error", error);
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChatBotApiException.class)
    public ResponseEntity<Map<String, String>> handleChatBotApiException(ChatBotApiException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidPromptException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPromptException(InvalidPromptException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiKeyNotConfiguredException.class)
    public ResponseEntity<Map<String, String>> handleApiKeyNotConfiguredException(ApiKeyNotConfiguredException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RedditOAuthException.class)
    public ResponseEntity<Map<String, String>> handleRedditOAuthException(RedditOAuthException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RedditFetchingException.class)
    public ResponseEntity<Map<String, String>> handleRedditFetchingException(RedditFetchingException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UpdaterServiceException.class)
    public ResponseEntity<Map<String, String>> handleUpdaterServiceException(UpdaterServiceException e) {
        return buildResponse(e, e.getStatus(), e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
