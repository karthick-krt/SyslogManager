package com.syslogmanager.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import com.syslogmanager.application.model.User;
import com.syslogmanager.application.model.Role;
import com.syslogmanager.application.repository.UsersRepository;
import com.syslogmanager.application.repository.RolesRepository;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TECHNICIAN')")
    public List<User> listUsers() {
        return usersRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TECHNICIAN')")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return usersRepository.findById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {
        if (usersRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        Role role = rolesRepository.findByName(req.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + req.getRole()));

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .active(true)
                .build();

        usersRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        return usersRepository.findById(id).map(user -> {
            if (req.getUsername() != null) user.setUsername(req.getUsername());
            if (req.getEmail() != null) user.setEmail(req.getEmail());
            if (req.getPassword() != null && !req.getPassword().isBlank()) user.setPassword(passwordEncoder.encode(req.getPassword()));
            if (req.getRole() != null) {
                Role role = rolesRepository.findByName(req.getRole()).orElseThrow(() -> new IllegalArgumentException("Role not found"));
                user.setRole(role);
            }
            if (req.getActive() != null) user.setActive(req.getActive());
            usersRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return usersRepository.findById(id).map(user -> {
            // Prevent deleting the default admin user
            if ("admin".equalsIgnoreCase(user.getUsername())) {
                return ResponseEntity.badRequest().body("Default admin user cannot be deleted");
            }
            usersRepository.delete(user);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private String role;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class UpdateUserRequest {
        private String username;
        private String email;
        private String password;
        private String role;
        private Boolean active;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }

}
