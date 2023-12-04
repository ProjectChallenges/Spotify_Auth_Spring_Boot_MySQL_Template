package com.spotifyApiAuth.authorizationDemo.repository;

import com.spotifyApiAuth.authorizationDemo.model.Spotify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotifyRepository extends JpaRepository<Spotify, Long> {
    Optional<Spotify> findByUserId(Integer userId);
}

