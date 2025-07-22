package org.example.authenticationservice.securityConfig;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.entity.User;
import org.example.authenticationservice.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );

        List<GrantedAuthority> roles = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                    .collect(Collectors.toList());

        return new CustomUserDetails(user.getEmail(), user.getPassword(), roles);
    }
}
