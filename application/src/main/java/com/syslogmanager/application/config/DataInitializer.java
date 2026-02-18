package com.syslogmanager.application.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.syslogmanager.application.model.User;
import com.syslogmanager.application.model.Role;
import com.syslogmanager.application.repository.UsersRepository;
import com.syslogmanager.application.repository.RolesRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements ApplicationRunner {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDefaultRoles();
        createDefaultAdminUser();
    }

    // Create default roles if they don't exist
    public void createDefaultRoles() {
        if (!rolesRepository.findByName("ADMIN").isPresent()) {
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Administrator role")
                    .build();
            rolesRepository.save(adminRole);
        }

        if (!rolesRepository.findByName("TECHNICIAN").isPresent()) {
            Role techRole = Role.builder()
                    .name("TECHNICIAN")
                    .description("Technician role")
                    .build();
            rolesRepository.save(techRole);
        }
    }

    // This method can be used to programmatically create a default admin user if needed.
    public void createDefaultAdminUser() {

        String adminUsername = "admin";

        if (usersRepository.existsByUsername(adminUsername)) {
            // If an admin user already exists, ensure the stored password is BCrypt-encoded.
            // This handles the case where a plaintext password was previously persisted.
            usersRepository.findByUsername(adminUsername).ifPresent(existing -> {
                String stored = existing.getPassword();
                if (stored == null || !stored.startsWith("$2")) {
                    String toEncode = (stored == null || stored.isEmpty()) ? "admin" : stored;
                    existing.setPassword(passwordEncoder.encode(toEncode));
                    usersRepository.save(existing);
                }
            });
            return;
        }

        Role adminRole = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        User admin = User.builder()
            .username(adminUsername)
            .email("admin@local.domain")
            .password(passwordEncoder.encode("admin"))
            .role(adminRole)
            .active(true)
            .build();

        usersRepository.save(admin);
    }

}
