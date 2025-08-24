package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.JournalEntryDTO;
import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.dto.UserJournalEntryDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.service.JournalService;
import com.portfolio.journalApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
