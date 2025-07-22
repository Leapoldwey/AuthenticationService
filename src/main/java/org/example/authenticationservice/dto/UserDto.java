package org.example.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String login;
    private String password;
    private String email;
    private Set<String> roles = new HashSet<>();
}
