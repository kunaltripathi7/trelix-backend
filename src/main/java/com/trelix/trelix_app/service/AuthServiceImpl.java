package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.response.AuthResponse;
import com.trelix.trelix_app.dto.request.LoginRequest;
import com.trelix.trelix_app.dto.request.RegisterRequest;
import com.trelix.trelix_app.dto.response.RefreshResult;
import com.trelix.trelix_app.dto.response.RegisterResponse;
import com.trelix.trelix_app.dto.response.UserResponse;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.ROLE;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.UnauthorizedException;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

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

        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getCreatedAt());
        return new RegisterResponse(userResponse, "Registration successful. Please login.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password", ErrorCode.AUTHENTICATION_FAILURE);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found after authentication",
                        ErrorCode.AUTHENTICATION_FAILURE));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtUtil.getAccessTokenExpiration());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        RefreshResult result = refreshTokenService.validateAndRotate(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(result.user());

        return new AuthResponse(
                newAccessToken,
                result.newToken(),
                "Bearer",
                jwtUtil.getAccessTokenExpiration());
    }

    @Override
    public void logout(String token) {
        System.out.println("User logged out (token discarded by client): " + token);
    }

}
