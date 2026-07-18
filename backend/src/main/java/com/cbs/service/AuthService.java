package com.cbs.service;

import com.cbs.model.User;
import com.cbs.repository.UserRepository;
import com.cbs.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("User not found"));
        String token = jwtUtil.generateToken(username, user.getRoles().stream().toList());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", username);
        result.put("roles", user.getRoles());
        return result;
    }

    @org.springframework.transaction.annotation.Transactional
    public User createUser(String username, String password, java.util.Set<String> roles) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}
