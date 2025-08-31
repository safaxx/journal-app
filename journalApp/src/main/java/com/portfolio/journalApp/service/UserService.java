package com.portfolio.journalApp.service;

import com.portfolio.journalApp.dto.UpdateProfileRequestDTO;
import com.portfolio.journalApp.dto.UserProfileDTO;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User saveUserInfo(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER"));
        return userRepository.save(user);
    }

    public User saveAdminUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER", "ADMIN"));
        return userRepository.save(user);
    }

    public User findUser(String username) {
        return userRepository.findByUsername(username);
    }
    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setRoles(user.getRoles());
        profile.setTotalEntries(user.getEntries() != null ? user.getEntries().size() : 0);

        if (user.getCreatedDate() != null) {
            profile.setMemberSince(user.getCreatedDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }

        return profile;
    }

    public User updateUserProfile(String username, UpdateProfileRequestDTO updateRequest) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return null;
        }
        boolean needsUpdate = false;
        if (updateRequest.getUsername() != null &&
                !updateRequest.getUsername().trim().isEmpty() &&
                !updateRequest.getUsername().equals(existingUser.getUsername())) {


            if (userRepository.findByUsername(updateRequest.getUsername()) != null) {
                throw new IllegalArgumentException("Username already exists");
            }
            existingUser.setUsername(updateRequest.getUsername());
            needsUpdate = true;
        }
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            needsUpdate = true;
        }


        if (updateRequest.getEmail() != null) {
            existingUser.setEmail(updateRequest.getEmail().trim().isEmpty() ? null : updateRequest.getEmail());
            needsUpdate = true;
        }

        if (needsUpdate) {
            return userRepository.save(existingUser);
        }

        return existingUser;
    }

    public void deleteUser(String existingUser) {
        userRepository.deleteByUsername(existingUser);
    }

    public void updateLastLoginDate(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}
