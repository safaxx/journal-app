package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final UserService userService;

    @PostMapping("/user/create")
    public ResponseEntity<ResponseDTO> createUser(@RequestBody User user){
        User newUser = userService.saveUserInfo(user);
        if(newUser!=null){
            return new ResponseEntity<>( new ResponseDTO("New user entry created", newUser),
                    HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ResponseDTO("User creation failed"),HttpStatus.BAD_REQUEST);
    }

}
