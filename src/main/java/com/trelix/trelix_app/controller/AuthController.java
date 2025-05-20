package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AuthenticationRequest;
import com.trelix.trelix_app.dto.AuthenticationResponse;
import com.trelix.trelix_app.dto.RefreshTokenRequest;
import com.trelix.trelix_app.dto.RegisterRequest;

import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.Role;

import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.UserService;
import com.trelix.trelix_app.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private  JwtUtils jwtUtils;

    @Autowired
    private UserService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request) {

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            // ResponseEntity<Object>(Object body, HttpStatus status)
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getEmail());
        User user = ((CustomUserDetails) userDetails).getUser();

        String accessToken = jwtUtils.generateTokenFromUsername(userDetails);

        String refreshToken = userDetailsService.generateAndSaveRefreshToken(user);

        return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        User user = userDetailsService.registerUser(request.getEmail(), request.getPassword(), request.getUsername(), Role.ROLE_USER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtils.generateTokenFromUsername(userDetails);
        String refreshToken = userDetailsService.generateAndSaveRefreshToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        User user = userDetailsService.findByRefreshToken(refreshToken);
        String newAccessToken = jwtUtils.generateTokenFromUsername(new CustomUserDetails(user));
        String newRefreshToken = userDetailsService.generateAndSaveRefreshToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtUtils.getJwtFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        String email = jwtUtils.getUserNameFromJwtToken(token);
        User user = userDetailsService.findByEmail(email);
        user.setRefreshToken(null);
        userDetailsService.save(user);
        return ResponseEntity.ok("Logged out successfully");
    }


}
