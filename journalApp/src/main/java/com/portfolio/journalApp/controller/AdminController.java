package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.JournalEntryDTO;
import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.dto.UserJournalEntryDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.service.JournalService;
import com.portfolio.journalApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JournalService service;
    private final UserService userService;


    @GetMapping("/journal-entry/all")
    public ResponseEntity<ResponseDTO> getAllJournalEntries() {
        List<UserJournalEntryDTO>  allEntries = service.getAllEntriesForAdmin();

        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(new ResponseDTO("Data fetched successfully", allEntries), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/journal-entry/id/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable String entryId) {
        Optional<JournalEntry> entry = service.findEntryById(entryId);
        if (entry.isPresent()) {
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.findAllUsers();
        if (users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>(users, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/user/create-admin")
    public ResponseEntity<ResponseDTO> createAdminUser(@RequestBody User user){
        User newUser = userService.saveAdminUser(user);
        if(newUser!=null){
            return new ResponseEntity<>( new ResponseDTO("New user entry created", newUser),
                    HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ResponseDTO("User creation failed"),HttpStatus.BAD_REQUEST);
    }
}
