package com.portfolio.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String username;
    private String email;
    private List<String> roles;
    private int totalEntries;
    private String memberSince;
}