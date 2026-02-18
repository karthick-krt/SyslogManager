package com.syslogmanager.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.syslogmanager.application.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);
    boolean existsBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
    // delete all sessions for a username
    void deleteAllByUsername(String username);
}
