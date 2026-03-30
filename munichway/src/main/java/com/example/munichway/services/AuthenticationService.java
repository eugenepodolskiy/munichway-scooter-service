package com.example.munichway.services;

import com.example.munichway.DTO.LoginRequest;
import com.example.munichway.DTO.TokenResponse;
import com.example.munichway.DTO.UserCreateRequest;
import com.example.munichway.models.User;
import com.example.munichway.repositories.UserRepository;
import com.example.munichway.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(UserCreateRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(com.example.munichway.enums.Role.USER);

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new TokenResponse(jwtToken);
    }

    public TokenResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String jwtToken = jwtService.generateToken(user);
        return new TokenResponse(jwtToken);
    }
}