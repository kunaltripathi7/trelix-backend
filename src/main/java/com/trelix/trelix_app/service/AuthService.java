package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.response.AuthResponse;
import com.trelix.trelix_app.dto.request.LoginRequest;
import com.trelix.trelix_app.dto.request.RegisterRequest;
import com.trelix.trelix_app.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
}




