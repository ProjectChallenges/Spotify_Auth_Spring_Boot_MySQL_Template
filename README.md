# Spotify API Authorization with Spring Boot

This is a Spring Boot project that demonstrates how to implement Spotify API authorization using the OAuth 2.0 authorization code flow. The project utilizes MySQL as the database to securely store access tokens.

## Architecture

The project follows the Model-View-Controller (MVC) architecture. Here's a brief overview of each component:

- **Controller (`SpotifyAuthorizationController.java`):**
  - Handles HTTP requests and manages the flow of data between the Model and View.
  - Exposes endpoints for user login (`/login`) and callback (`/callback`).
  - Initiates the Spotify authorization process and handles the callback from Spotify.

- **Model (`Spotify.java`):**
  - Represents the Spotify entity, which includes properties like access token, user ID, and expiration timestamp.
  - Annotated as a JPA entity to be stored in the MySQL database.

- **Repository (`SpotifyRepository.java`):**
  - Provides CRUD operations for the Spotify entity.
  - Extends `JpaRepository` for convenient data access.

- **Service (`SpotifyAuthorizationService.java`):**
  - Handles the Spotify authorization logic, including building the authorization URL, exchanging code for an access token, and storing/updating the token in the database.
  - Uses RestTemplate for making HTTP requests to the Spotify API.

## Database Schema

The MySQL database schema includes two tables: `spotify` for storing access tokens.

```sql
-- Create database if not exists
CREATE DATABASE IF NOT EXISTS spoti_api;

USE spoti_api;

-- Table for storing Spotify access tokens
CREATE TABLE IF NOT EXISTS spotify_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    access_token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

```

## How to Use the Application

### 1. Clone the Repository

```bash
git clone <repository-url>
cd authorizationDemo
```

### 2. Set up MySQL Database

- Create a MySQL database named `spoti_api`.
- Adjust database connection properties in `src/main/resources/application.properties`.

### 3. Set up Spotify API Credentials

- Obtain your Spotify API credentials (client ID, client secret).
- Set them in `src/main/resources/application.properties`.

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

### 5. Access the Application

- Open your web browser and go to `http://localhost:8080`.
- Click on "Login with Spotify" to initiate the authorization process.

### 6. Spotify Authorization Flow

1. **Login (`/login`):**
   - Click on "Login with Spotify" to redirect to the Spotify login page.

2. **Callback (`/callback`):**
   - After successful login, you will be redirected to the callback URL with a code.
   - The code is exchanged for an access token with Spotify.

3. **Token Storage in MySQL:**
   - The access token is stored securely in the MySQL database along with the user ID and expiration timestamp.

4. **Accessing Playlist Data:**
   - Further functionalities, such as accessing and managing Spotify playlists, can be added by extending the application.

## Code Snippets

- [SpotifyAuthorizationController.java](src/main/java/com/spotifyApiAuth/authorizationDemo/controller/SpotifyAuthorizationController.java)
- [Spotify.java](src/main/java/com/spotifyApiAuth/authorizationDemo/model/Spotify.java)
- [SpotifyRepository.java](src/main/java/com/spotifyApiAuth/authorizationDemo/repository/SpotifyRepository.java)
- [SpotifyAuthorizationService.java](src/main/java/com/spotifyApiAuth/authorizationDemo/service/SpotifyAuthorizationService.java)
- [AuthorizationDemoApplication.java](src/main/java/com/spotifyApiAuth/authorizationDemo/AuthorizationDemoApplication.java)

## Note

- Ensure that the MySQL database is running and accessible.
- This project provides a basic setup for Spotify API authorization and token storage. Extend it based on your application requirements.

Certainly! Below are code snippets for each component of the architecture:

### Controller (`SpotifyAuthorizationController.java`):

```java
// Endpoint for initiating Spotify authorization
@GetMapping("/login")
public String login(HttpSession session) {
    // Implementation...
    return "redirect:" + spotifyAuthorizationUrl;
}

// Callback endpoint after successful Spotify login
@GetMapping("/callback")
public String callback(@RequestParam("code") String code,
                       @RequestParam("state") String state,
                       HttpSession session,
                       Model model) {
    // Implementation...
    return "callback";
}
```

### Model (`Spotify.java`):

```java
@Entity
@Table(name = "spotify")
public class Spotify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Constructors, getters, setters...
}
```

### Repository (`SpotifyRepository.java`):

```java
public interface SpotifyRepository extends JpaRepository<Spotify, Long> {
    Optional<Spotify> findByUserId(Integer userId);
}
```

### Service (`SpotifyAuthorizationService.java`):

```java
// Building Spotify authorization URL
public String buildAuthorizationUrl(String state) {
    // Implementation...
    return builder.build(true).toUriString();
}

// Requesting and storing access token
public String requestAccessToken(String code, HttpSession session, Integer userId) {
    // Implementation...
    return accessToken;
}
```

### Main Application Class (`AuthorizationDemoApplication.java`):

```java
@SpringBootApplication
public class AuthorizationDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationDemoApplication.class, args);
    }
}
```
# Simple Response Body example with no nested Arrays
```json
[
    {
        "id": "37i9dQZEVXbJiZcmkrIHGU",
        "collaborative": false,
        "description": "Dein tägliches Update zu den aktuell am häufigsten gespielten Songs – Deutschland.",
        "href": "https://api.spotify.com/v1/playlists/37i9dQZEVXbJiZcmkrIHGU",
        "name": "Top 50 – Deutschland",
        "snapshot_id": "NzU0OTkzMjAwLDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDY0NjU=",
        "status": false,
        "type": "playlist",
        "uri": "spotify:playlist:37i9dQZEVXbJiZcmkrIHGU"
    }
]
```

These code snippets provide a concise representation of the key functionalities in each component. Make sure to refer to the full source code for a comprehensive understanding of the implementation details.
