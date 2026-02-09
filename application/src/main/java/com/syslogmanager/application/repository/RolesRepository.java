package com.syslogmanager.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.syslogmanager.application.model.Role;

public interface RolesRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
