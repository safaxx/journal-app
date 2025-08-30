package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        User u = userService.findUser(username);
        if (u != null) {
            return new ResponseEntity<>(u, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDTO("User " + username + " does not exist"), HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateUserByUsername(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestBody User changedUser) {
        /***
         * 1st method - Spring automatically injects the currently authenticated principal (the UserDetails object returned by your CustomUserDetailsService).
         * 2nd method - Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         *You manually pull the authentication info from the thread-local SecurityContext
         * This is lower-level, but it gives you full access to the entire Authentication object (roles, authorities, credentials, details, etc.).
         */
        //System.out.println(userDetails.getAuthorities());
        User existingUser = userService.findUser(userDetails.getUsername());
        User updatedUser = userService.updateUserDetails(existingUser, changedUser);
        return new ResponseEntity<>(new ResponseDTO("User info updated successfully", updatedUser), HttpStatus.OK);


    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
        User existingUser = userService.findUser(userDetails.getUsername());
        userService.deleteUser(userDetails.getUsername());
        return new ResponseEntity<>(new ResponseDTO("User deleted successfully"), HttpStatus.NO_CONTENT);
    }


}
