package com.trelix.trelix_app.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
}




