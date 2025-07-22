package org.example.authenticationservice.service;

import org.example.authenticationservice.dto.UserDto;
import org.example.authenticationservice.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto save(UserDto userDto);
    UserDto update(UserDto userDto);
    UserDto findById(UUID id);
    UserDto findByEmail(String email);
    List<UserDto> findAll();
    boolean existsByEmail(String email);
    boolean existsByLogin(String email);
    void deleteById(UUID id);
}
