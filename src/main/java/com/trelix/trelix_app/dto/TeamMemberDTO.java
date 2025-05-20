package com.trelix.trelix_app.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDTO {
    private UUID id;
    private String username;
    private String email;
    private String role;
}