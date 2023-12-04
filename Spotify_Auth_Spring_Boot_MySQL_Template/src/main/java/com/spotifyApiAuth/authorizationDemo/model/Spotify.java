package com.spotifyApiAuth.authorizationDemo.model;


import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Table(name="spotify")
public class Spotify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    public Spotify() {

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "user_id")
    private Integer userId;  // Add a user_id field

    @Column(name="access_Token")
    private String accessToken;




    @Column(name = "expires_At", nullable = false)
    private LocalDateTime expiresAt;


    // Constructors, getters, setters, etc.


    public Spotify(Long id, Integer userId, String accessToken, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }



}
