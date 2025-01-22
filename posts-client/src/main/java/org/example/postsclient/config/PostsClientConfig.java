package org.example.postsclient.config;

import org.example.postsclient.PostsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class PostsClientConfig {
    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String clientSecret;

    @Value("${reddit.user.agent}")
    private String userAgent;

    @Value("${reddit.token.url}")
    private String tokenUrl;

    @Value("${reddit.user.name}")
    private String username;

    @Value("${reddit.user.password}")
    private String password;

    @Bean
    public RedditConfig RedditConfig(
            @Value("${reddit.client.id}") String clientId, @Value("${reddit.client.secret}") String clientSecret, @Value("${reddit.user.agent}") String userAgent, @Value("${reddit.token.url}") String tokenUrl, @Value("${reddit.user.name}") String username, @Value("${reddit.user.password}") String password
    ) {
        return new RedditConfig(clientId, clientSecret, userAgent, tokenUrl, username, password);
    }

    @Bean
    public PostsClient postsClient(RedditConfig redditConfig) {
        return new PostsClient(redditConfig);
    }
}
