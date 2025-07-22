package org.example.authenticationservice.service;

import org.example.authenticationservice.entity.Role;

public interface RoleService {
    Role findByName(String roleName);
}
