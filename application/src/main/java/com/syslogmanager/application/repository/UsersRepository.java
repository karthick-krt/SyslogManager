package com.syslogmanager.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.syslogmanager.application.model.User;

public interface UsersRepository extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
