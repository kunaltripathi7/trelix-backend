package com.trelix.trelix_app.dto;


import com.trelix.trelix_app.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentRequest {
    @NotNull
    @Email(message = "Email should be valid")
    private String email;

    @NotNull
    private Role newRole;
}

