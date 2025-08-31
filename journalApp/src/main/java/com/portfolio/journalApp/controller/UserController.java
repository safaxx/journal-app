package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.dto.UpdateProfileRequestDTO;
import com.portfolio.journalApp.dto.UserProfileDTO;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserProfileDTO profile = userService.getUserProfile(userDetails.getUsername());
            if (profile != null) {
                return new ResponseEntity<>(
                        new ResponseDTO(true, "Profile retrieved successfully", profile),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(new ResponseDTO(false, "User profile not found"), HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO("Error retrieving profile: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                            @Valid @RequestBody UpdateProfileRequestDTO updateRequest) {
        /***
         * 1st method - Spring automatically injects the currently authenticated principal (the UserDetails object returned by your CustomUserDetailsService).
         * 2nd method - Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         *You manually pull the authentication info from the thread-local SecurityContext
         * This is lower-level, but it gives you full access to the entire Authentication object (roles, authorities, credentials, details, etc.).
         */
        //System.out.println(userDetails.getAuthorities());
        try{
        User updatedUser = userService.updateUserProfile(userDetails.getUsername(), updateRequest);
        if (updatedUser != null) {
            return new ResponseEntity<>(
                    new ResponseDTO("Profile updated successfully", updatedUser.getUsername()),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(
                new ResponseDTO("Profile update failed"),
                HttpStatus.BAD_REQUEST
        );
    } catch (Exception e) {
        return new ResponseEntity<>(
                new ResponseDTO("Error updating profile: " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    }

    @DeleteMapping("/profile")
    public ResponseEntity<ResponseDTO> deleteUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.deleteUser(userDetails.getUsername());
            return new ResponseEntity<>(
                    new ResponseDTO("User account deleted successfully"),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO("Error deleting account: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    //backward compatibility lol
    @GetMapping("/{username}")
    public ResponseEntity<ResponseDTO> getUserByUsername(@PathVariable String username) {
        User u = userService.findUser(username);
        if (u != null) {
            return new ResponseEntity<>(
                    new ResponseDTO("User found", u),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(
                new ResponseDTO("User " + username + " does not exist"),
                HttpStatus.NOT_FOUND
        );
    }


}
