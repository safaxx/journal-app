package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.AuthResponseDTO;
import com.portfolio.journalApp.dto.LoginRequestDTO;
import com.portfolio.journalApp.dto.RegisterRequestDTO;
import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.security.CustomUserDetailsService;
import com.portfolio.journalApp.service.UserService;
import com.portfolio.journalApp.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;

    @GetMapping("/health-check")
    public ResponseEntity<ResponseDTO> healthCheck() {
        return new ResponseEntity<>(new ResponseDTO(true, "OK"), HttpStatus.OK);
    }

    @PostMapping("/user/sign-up")
    public ResponseEntity<ResponseDTO> signUp(@Valid @RequestBody RegisterRequestDTO requestDTO) {

        try {
            if (userService.findUser(requestDTO.getUsername()) != null) {
                return new ResponseEntity<>(new ResponseDTO("User already exists"), HttpStatus.BAD_REQUEST);
            }

            User user = new User();
            user.setUsername(requestDTO.getUsername());
            user.setPassword(requestDTO.getPassword());
            user.setEmail(requestDTO.getEmail());

            User newUser = userService.saveUserInfo(user);
            if (newUser != null) {
                return new ResponseEntity<>(new ResponseDTO(true, "User registered successfully", newUser),
                        HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new ResponseDTO(false, "User registration failed"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(false, "User registration failed" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(requestDTO.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            AuthResponseDTO authResponse = new AuthResponseDTO(jwt, userDetails.getUsername());
            userService.updateLastLoginDate(userDetails.getUsername());
            return new ResponseEntity<>(new ResponseDTO(true, "Login successful", authResponse), HttpStatus.OK);


        } catch (BadCredentialsException e) {
            log.error("Exception occurred while login", e);
            return new ResponseEntity<>(new ResponseDTO(false, "Incorrect username or password"), HttpStatus.UNAUTHORIZED);

        }catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Login failed: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(HttpServletRequest request) {
        // With JWT, logout is typically handled on the client side by removing the token
        // For server-side logout, you'd need to implement a token blacklist

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Here you could add the token to a blacklist if needed
            // For now, we'll just return a success message
        }

        return new ResponseEntity<>(
                new ResponseDTO("Logout successful"),
                HttpStatus.OK
        );
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ResponseDTO> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    return new ResponseEntity<>(
                            new ResponseDTO("Token is valid", username),
                            HttpStatus.OK
                    );
                }
            }

            return new ResponseEntity<>(
                    new ResponseDTO("Invalid token"),
                    HttpStatus.UNAUTHORIZED
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO("Token validation failed"),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @PostMapping("/admin/sign-up")
    public ResponseEntity<ResponseDTO> signUpAdminUser(@RequestBody RegisterRequestDTO requestDTO) {

        try {
            if (userService.findUser(requestDTO.getUsername()) != null) {
                return new ResponseEntity<>(new ResponseDTO("User already exists"), HttpStatus.BAD_REQUEST);
            }

            User user = new User();
            user.setUsername(requestDTO.getUsername());
            user.setPassword(requestDTO.getPassword());
            user.setEmail(requestDTO.getEmail());

            User newUser = userService.saveAdminUser(user);
            if (newUser != null) {
                return new ResponseEntity<>(new ResponseDTO(true, "Admin user registered successfully", newUser),
                        HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new ResponseDTO(false, "Admin registration failed"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(false, "Admin registration failed" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
