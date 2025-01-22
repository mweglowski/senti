package org.example.postsclient;

import org.example.exceptionhandler.exception.RedditFetchingException;
import org.example.exceptionhandler.exception.RedditOAuthException;
import org.example.postsclient.config.RedditConfig;
import org.example.postsclient.contract.CommentDTO;
import org.example.postsclient.contract.PostDTO;
import org.example.postsclient.utils.DateTimeConverter;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PostsClient {
    private final RestTemplate restClient;
    private final DateTimeConverter dateTimeConverter;
    private final RedditConfig redditConfig;

    public PostsClient(RedditConfig redditConfig) {
        this.restClient = new RestTemplate();
        this.dateTimeConverter = new DateTimeConverter();
        this.redditConfig = redditConfig;
    }

    private String getAccessToken() {
        try {

            String requestBody = "grant_type=password&username=" + redditConfig.username() + "&password=" + redditConfig.password();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", redditConfig.userAgent());

            String auth = redditConfig.clientId() + ":" + redditConfig.clientSecret();

            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restClient.exchange(redditConfig.tokenUrl(), HttpMethod.POST, request, Map.class);

            String accessToken = (String) response.getBody().get("access_token");

            return accessToken;
        } catch (Exception e) {
            throw new RedditOAuthException("Error occurred while trying to get reddit token.");
        }
    }

    public List<PostDTO> fetchPosts(String subreddit) {
        try {

            List<PostDTO> postDTOs = new ArrayList<>();
            // parameters are hardcoded like limit of posts, sorted from newest

            String postsUrl = "https://oauth.reddit.com/r/" + subreddit;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            headers.set("User-Agent", redditConfig.userAgent());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restClient.exchange(postsUrl, HttpMethod.GET, httpEntity, Map.class);

            Map responseBody = response.getBody();

            Map responseData = (Map) responseBody.get("data");
            List<Map> posts = (List<Map>) responseData.get("children");

            for (Map postObject : posts) {
                Map postData = (Map) postObject.get("data");

                String sourceId = postData.get("id").toString();
                String title = postData.get("title").toString();
                String content = postData.get("selftext").toString();
                String authorUsername = postData.get("author").toString();
                LocalDateTime createdAt = dateTimeConverter.convertUnixTimestamp((double) postData.get("created"));

                PostDTO postDTO = new PostDTO();
                postDTO.setSourceId(sourceId);
                postDTO.setContent(content);
                postDTO.setAuthorUsername(authorUsername);
                postDTO.setTitle(title);
                postDTO.setCreatedAt(createdAt);

                postDTOs.add(postDTO);

    //            System.out.println("SourceId: " + sourceId);
    //            System.out.println("Title: " + title);
    //            System.out.println("Content: " + content);
    //            System.out.println("Author: " + authorUsername);
    //            System.out.println("CreatedAt: " + createdAt);
            }

            return postDTOs;
        } catch (Exception e) {
            throw new RedditFetchingException("Error occurred while fetching posts from reddit.");
        }
    }

    public List<CommentDTO> fetchComments(String subreddit, String postId) {
        try {

            List<CommentDTO> commentDTOs = new ArrayList<>();

            String accessToken = getAccessToken();

            String commentsUrl = "https://oauth.reddit.com/r/" + subreddit + "/comments/" + postId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("User-Agent", redditConfig.userAgent());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Map[]> response = restClient.exchange(commentsUrl, HttpMethod.GET, httpEntity, Map[].class);

            Map[] data = response.getBody();
            Map commentsData = data[1];
            List<Map> commentsChildren = (List<Map>) ((Map) commentsData.get("data")).get("children");

            for (Map child : commentsChildren) {
                Map commentData = (Map) child.get("data");

                String authorUsername = (String) commentData.get("author");
                String content = (String) commentData.get("body");
                LocalDateTime createdAt = dateTimeConverter.convertUnixTimestamp((double) commentData.get("created"));

                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setContent(content);
                commentDTO.setAuthorUsername(authorUsername);
                commentDTO.setCreatedAt(createdAt);

                commentDTOs.add(commentDTO);
            }

            return commentDTOs;
        } catch (Exception e) {
            throw new RedditFetchingException("Error occurred while fetching comments from reddit.");
        }
    }
}
