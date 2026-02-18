package com.syslogmanager.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import com.syslogmanager.application.repository.UsersRepository;
import com.syslogmanager.application.model.User;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;

    private final com.syslogmanager.application.security.JwtUtil jwtUtil;
    private final com.syslogmanager.application.repository.SessionRepository sessionRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = usersRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found after authentication");
        }

        // create server-side session id and include in token
        String sessionId = java.util.UUID.randomUUID().toString();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName(), sessionId);
        // decode token to get expiration
        com.auth0.jwt.interfaces.DecodedJWT decoded = jwtUtil.validateToken(token);
        java.util.Date expires = decoded.getExpiresAt();
        // persist session
        sessionRepository.save(new com.syslogmanager.application.model.Session(sessionId, user.getUsername(), expires));

        return ResponseEntity.ok(new LoginResponse(token, new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().getName())));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(org.springframework.http.HttpEntity<String> httpEntity, jakarta.servlet.http.HttpServletRequest request) {
        // Try to read token from Authorization header
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("No token provided");
        }

        try {
            com.auth0.jwt.interfaces.DecodedJWT decoded = jwtUtil.validateToken(token);
            String username = decoded.getSubject();
            if (username != null) {
                // invalidate all sessions for this user
                sessionRepository.deleteAllByUsername(username);
            }
            return ResponseEntity.ok("Logged out");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    public static record UserResponse(Long id, String username, String email, String role) {}

    public static record LoginResponse(String token, UserResponse user) {}

}
