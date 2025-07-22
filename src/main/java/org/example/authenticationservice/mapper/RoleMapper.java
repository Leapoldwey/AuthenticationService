package org.example.authenticationservice.mapper;

import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.repository.RoleRepository;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RoleMapper {
    @Autowired
    private RoleRepository roleRepository;

    @Named("mapRolesToStrings")
    public Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    @Named("mapStringsToRoles")
    public Set<Role> mapStringsToRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        List<Role> allRoles = roleRepository.findAll();
        Map<String, Role> roleMap = allRoles.stream()
                .collect(Collectors.toMap(role -> role.getName().name(), role -> role));

        return roles.stream()
                .map(roleMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
