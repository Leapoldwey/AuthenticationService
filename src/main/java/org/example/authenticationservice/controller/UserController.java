package org.example.authenticationservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.UserDto;
import org.example.authenticationservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        var user = userService.save(userDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        var user = userService.update(userDto);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") String id) {
        var user = userService.findById(UUID.fromString(id));

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteById(UUID.fromString(id));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
