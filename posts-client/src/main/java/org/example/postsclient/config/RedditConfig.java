package org.example.postsclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

public record RedditConfig(String clientId, String clientSecret, String userAgent, String tokenUrl, String username, String password) {
    public RedditConfig(@Value("${reddit.client.id}") String clientId, @Value("${reddit.client.secret}") String clientSecret, @Value("${reddit.user.agent}") String userAgent, @Value("${reddit.token.url}") String tokenUrl, @Value("${reddit.user.name}") String username, @Value("${reddit.user.password}") String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userAgent = userAgent;
        this.tokenUrl = tokenUrl;
        this.username = username;
        this.password = password;
    };
}
