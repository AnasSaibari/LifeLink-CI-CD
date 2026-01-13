package com.app.userService.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Email ou username est requis")
    private String emailOrUsername;
    
    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}

