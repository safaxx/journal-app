package com.portfolio.journalApp.controller;


import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.exceptions.ResourceNotFoundException;
import com.portfolio.journalApp.service.JournalService;
import com.portfolio.journalApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService service;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllJournalEntries() {
        List<JournalEntry> allEntries = service.getAllEntries();
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all/{username}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String username) {
        List<JournalEntry> allEntries = service.getAllEntries(username);
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDTO("No entries found for "+username),HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create/{username}")
    public ResponseEntity<ResponseDTO> createEntryForUser(@RequestBody JournalEntry entry, @PathVariable String username) {
        JournalEntry newEntry = service.saveEntry(entry, username);
        if (newEntry != null) {
            return new ResponseEntity<>(
                    new ResponseDTO("New journal entry created", newEntry),
                    HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ResponseDTO("New journal entry creation failed"),
                HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/id/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable String entryId) {
        Optional<JournalEntry> entry = service.findEntryById(entryId);
        if (entry.isPresent()) {
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{entryId}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable String entryId,
                                                        @RequestBody JournalEntry newEntry) throws ResourceNotFoundException {
        JournalEntry updatedEntry = service.updateEntry(entryId, newEntry);
        return updatedEntry != null
                ? new ResponseEntity<>(updatedEntry, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/delete/{username}/{id}")
    public ResponseEntity<?> deleteEntryOfUser(@PathVariable String id, @PathVariable String username){
        service.deleteEntry(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
