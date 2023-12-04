package com.spotifyApiAuth.authorizationDemo.controller;

import com.spotifyApiAuth.authorizationDemo.model.Spotify;
import com.spotifyApiAuth.authorizationDemo.repository.SpotifyRepository;
import com.spotifyApiAuth.authorizationDemo.service.SpotifyAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

@Controller
public class SpotifyAuthorizationController {

    private final SpotifyAuthorizationService spotifyAuthorizationService;
    private final SpotifyRepository spotifyRepository;  // Add the repository

    @Autowired
    public SpotifyAuthorizationController(
            SpotifyAuthorizationService spotifyAuthorizationService,
            SpotifyRepository spotifyRepository) {  // Autowire the repository
        this.spotifyAuthorizationService = spotifyAuthorizationService;
        this.spotifyRepository = spotifyRepository;  // Initialize the repository
    }


    @GetMapping("/login")
    public String login(HttpSession session) {
        // Generate a random state and store it in the session
        String state = spotifyAuthorizationService.generateRandomState();
        session.setAttribute("spotify_state", state);




        System.out.println("HttpSession: " + session);
        System.out.println("state: " + state);

        // Redirect to Spotify authorization URL
        String spotifyAuthorizationUrl = spotifyAuthorizationService.buildAuthorizationUrl(state);
        return "redirect:" + spotifyAuthorizationUrl;


    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpSession session,
                           Model model) {

        // Log the received parameters
        System.out.println("Received code: " + code);
        System.out.println("Received state: " + state);




        // Retrieve the stored state from the session
        String storedState = (String) session.getAttribute("spotify_state");

        // Check state for security
        if (storedState != null && storedState.equals(state)) {
            // State is valid, proceed with authorization

            // Get userId from the session
            String userIdStr = (String) session.getAttribute("userId");
            Integer userId = userIdStr != null ? Integer.parseInt(userIdStr) : null;

            // Request access token
            String accessToken = spotifyAuthorizationService.requestAccessToken(code, session, userId);


            if (accessToken != null) {
                // Create a new Spotify entity and set the properties
                Spotify spotifyEntity = new Spotify();
                spotifyEntity.setAccessToken(accessToken);
                spotifyEntity.setUserId(userId);
                // Set the expiration timestamp as needed
                spotifyEntity.setExpiresAt(LocalDateTime.now().plusSeconds(3600)); // Assuming expires_in is in seconds

                // Save the Spotify entity
                spotifyRepository.save(spotifyEntity);

            // Request access token
            //accessToken = spotifyAuthorizationService.requestAccessToken(code, session, userId);


            System.out.println("Access Token: " + accessToken);

            // Store accessToken securely (consider using a database)
            model.addAttribute("accessToken", accessToken);

            // Clear the state from the session after use
            session.removeAttribute("spotify_state");

            // Log other relevant information
            System.out.println("Redirecting to callback page");

            return "callback";
        } else {
            // Invalid state, handle appropriately (e.g., redirect to an error page)
            return "error";
        }
        } else {
            // Invalid state, handle appropriately (e.g., redirect to an error page)
            return "error";
        }
    }
}

