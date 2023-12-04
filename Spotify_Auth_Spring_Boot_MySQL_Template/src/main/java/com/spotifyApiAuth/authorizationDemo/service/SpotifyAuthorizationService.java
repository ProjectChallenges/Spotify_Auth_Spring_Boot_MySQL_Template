package com.spotifyApiAuth.authorizationDemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyApiAuth.authorizationDemo.model.Spotify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import com.spotifyApiAuth.authorizationDemo.repository.SpotifyRepository;

@Service
public class SpotifyAuthorizationService {

    private final SpotifyRepository spotifyRepository;

    @Autowired
    public SpotifyAuthorizationService(SpotifyRepository spotifyRepository) {
        this.spotifyRepository = spotifyRepository;
    }

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;


    public String buildAuthorizationUrl(String state) {
        String scope = String.join(" ", "user-read-private", "user-read-email");
        String redirectUri = "http://localhost:8080/callback";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://accounts.spotify.com/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", encodeValue(scope))  // URL-encode the scope
                .queryParam("state", state);

        System.out.println("Redirect URI: " + redirectUri);

        System.out.println("Constructed URL: " + builder.toUriString());

        return builder.build(true).toUriString();
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public String requestAccessToken(String code, HttpSession session, Integer userId) {
        // Retrieve the stored state from the session
        String storedState = (String) session.getAttribute("spotify_state");

        System.out.println("User ID: " + userId );

        // Check state for security
        if (storedState != null) {
            // Set up the request to exchange the authorization code for an access token
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(clientId, clientSecret);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://accounts.spotify.com/api/token")
                    .queryParam("code", code)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("grant_type", "authorization_code");

            RequestEntity<?> requestEntity = RequestEntity
                    .post(URI.create(builder.toUriString()))
                    .headers(headers)
                    .build();

            // Make the request using RestTemplate
            ResponseEntity<String> responseEntity = new RestTemplate().exchange(requestEntity, String.class);

            String responseBody = responseEntity.getBody();
            System.out.println("Response Body: " + responseBody);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // Parse the JSON response manually or use a JSON parsing library
                // For simplicity, let's manually extract the access_token from the JSON
                String accessToken = extractAccessTokenFromJson(responseBody);

                // Calculate expiration time (current time + expiresIn seconds)
                Long expiresIn = extractExpiresInFromJson(responseBody);
                LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expiresIn);



                // Ensure userId is not null before storing or updating the access token in the database
                if (userId != null) {
                    // Store or update the access token in the database
                    storeOrUpdateAccessToken(userId, accessToken, expiresAt);

                    System.out.println("User ID: " + userId);

                } else {
                    // Log or handle the case where userId is null
                    System.out.println("User ID is null. Access token was stored.");
                }

                return accessToken;
            } else {
                // Handle error, log, or throw an exception
                return null;
            }
        } else {
            // Invalid state, handle appropriately (e.g., redirect to an error page)
            return null;
        }
    }

    private String extractAccessTokenFromJson(String responseBody) {
        // Parse the JSON response manually or use a JSON parsing library
        // For simplicity, let's manually extract the access_token from the JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
    }

    private void storeOrUpdateAccessToken(Integer userId, String accessToken, LocalDateTime expiresAt) {
        // Check if there is an existing record for the user
        Optional<Spotify> existingRecord = spotifyRepository.findByUserId(userId);

        if (existingRecord.isPresent()) {
            // Update the existing record
            Spotify spotify = existingRecord.get();
            spotify.setAccessToken(accessToken);
            spotify.setExpiresAt(expiresAt);
            spotifyRepository.save(spotify);
        } else {
            // Create a new record
            Spotify newRecord = new Spotify();
            newRecord.setUserId(userId);
            newRecord.setAccessToken(accessToken);
            newRecord.setExpiresAt(expiresAt);
            spotifyRepository.save(newRecord);
        }
    }

    private Long extractExpiresInFromJson(String responseBody) {
        // Parse the JSON response and extract the 'expires_in' field
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("expires_in").asLong();
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
    }








    public String generateRandomState() {
        // Implement logic to generate a random state
        // For simplicity, you can use a UUID
        return java.util.UUID.randomUUID().toString();
    }

    private static class AccessTokenResponse {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }
    }
}
