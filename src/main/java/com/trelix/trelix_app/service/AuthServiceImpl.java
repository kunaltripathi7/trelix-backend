package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AuthResponse;
import com.trelix.trelix_app.dto.LoginRequest;
import com.trelix.trelix_app.dto.RegisterRequest;
import com.trelix.trelix_app.dto.RegisterResponse;
import com.trelix.trelix_app.dto.UserResponse;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.ROLE;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.UnauthorizedException;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered: " + request.email(), ErrorCode.DATABASE_CONFLICT);
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now())
                .role(ROLE.USER)
                .enabled(true)
                .updatedAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
        return new RegisterResponse(userResponse, "Registration successful. Please login.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid email or password", ErrorCode.AUTHENTICATION_FAILURE);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found after authentication", ErrorCode.AUTHENTICATION_FAILURE));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900 // 15 minutes in seconds
        );
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            String userEmail = jwtUtil.extractEmail(refreshToken);
            UUID userId = jwtUtil.extractUserId(refreshToken);

            if (userEmail != null && jwtUtil.isTokenValid(refreshToken, userDetailsService.loadUserByUsername(userEmail))) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UnauthorizedException("User not found for refresh token", ErrorCode.AUTHENTICATION_FAILURE));

                String newAccessToken = jwtUtil.generateAccessToken(user);

                return new AuthResponse(
                        newAccessToken,
                        refreshToken,
                        "Bearer",
                        900
                );
            }
            throw new UnauthorizedException("Invalid or expired refresh token", ErrorCode.AUTHENTICATION_FAILURE);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired refresh token: " + e.getMessage(), ErrorCode.AUTHENTICATION_FAILURE);
        }
    }

    @Override
    public void logout(String token) {
        System.out.println("User logged out (token discarded by client): " + token);
    }
}
