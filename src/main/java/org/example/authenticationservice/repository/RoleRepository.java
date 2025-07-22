package org.example.authenticationservice.repository;

import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.enumer.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByName(RoleType name);
}
