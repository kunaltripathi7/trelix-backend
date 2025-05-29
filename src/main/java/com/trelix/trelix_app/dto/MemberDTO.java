package com.trelix.trelix_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON serialization
public class MemberDTO {
    private UUID id;
    private String username;
    private String email;
    private String role;
}