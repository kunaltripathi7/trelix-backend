package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AuthResponse;
import com.trelix.trelix_app.dto.LoginRequest;
import com.trelix.trelix_app.dto.RegisterRequest;
import com.trelix.trelix_app.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
}
