package com.portfolio.journalApp.service;

import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    public User updateUserDetails(User existingUser, User updatedUser) {
        existingUser.setUsername(updatedUser.getUsername()!=null ? updatedUser.getUsername() : existingUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword()!=null ? updatedUser.getPassword() : existingUser.getPassword());
        if(existingUser.getRoles().contains("ADMIN")){
            saveAdminUser(existingUser);
        }else{
            saveUserInfo(existingUser);
        }
        return existingUser;
    }

    public void deleteUser(String existingUser) {
        userRepository.deleteByUsername(existingUser);
    }
}
