package com.portfolio.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";
    private String username;

    public AuthResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }
}