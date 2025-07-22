package org.example.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.UserDto;
import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.entity.User;
import org.example.authenticationservice.enumer.RoleType;
import org.example.authenticationservice.exception.customExceptions.UserAlreadyExistsException;
import org.example.authenticationservice.exception.customExceptions.UserNotFoundException;
import org.example.authenticationservice.mapper.UserMapper;
import org.example.authenticationservice.repository.RoleRepository;
import org.example.authenticationservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto save(UserDto userDto) {
        User user = userMapper.mapTo(userDto);
        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            Role role = roleRepository.findRoleByName(RoleType.GUEST).orElseThrow(
                    () -> new NoSuchElementException("No role found")
            );
            user.setRoles(Set.of(role));
        }

        userRepository.save(user);

        return userMapper.mapTo(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        if (userDto.getId() == null || !userRepository.existsById(userDto.getId())) {
            throw new UserNotFoundException("User with id " + userDto.getId() + " not found");
        }
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new UserNotFoundException("User with id " + userDto.getId() + " not found")
        );
        userMapper.updateUserFromDto(user, userDto);

        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }

        if (userDto.getLogin() != null && userRepository.existsByLogin(userDto.getLogin())) {
            throw new UserAlreadyExistsException("User with login " + userDto.getLogin() + " already exists");
        }

        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userRepository.save(user);

        return userMapper.mapTo(user);
    }

    @Override
    public UserDto findById(UUID id) {
        return userMapper.mapTo(
                userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"))
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByLogin(String email) {
        return userRepository.existsByLogin(email);
    }

    @Override
    public UserDto findByEmail(String email) {
        return userMapper.mapTo(
                userRepository
                        .findByEmail(email).orElseThrow(() ->
                                new UserNotFoundException("User with email " + email + " not found")
                        )
        );
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapTo)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }
}
