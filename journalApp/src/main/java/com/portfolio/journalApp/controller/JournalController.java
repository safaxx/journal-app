package com.portfolio.journalApp.controller;


import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.exceptions.ResourceNotFoundException;
import com.portfolio.journalApp.service.JournalService;
import com.portfolio.journalApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> getAllJournalEntriesOfUser(@AuthenticationPrincipal UserDetails userDetails) {
        List<JournalEntry> allEntries = service.getAllEntries(userDetails.getUsername());
        if (allEntries != null && !allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDTO("No entries found for: "+ userDetails.getUsername()),HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createEntryForUser(@RequestBody JournalEntry entry, @AuthenticationPrincipal UserDetails userDetails) {
        JournalEntry newEntry = service.saveEntry(entry, userDetails.getUsername());
        if (newEntry != null) {
            return new ResponseEntity<>(
                    new ResponseDTO("New journal entry created", newEntry),
                    HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ResponseDTO("New journal entry creation failed"),
                HttpStatus.BAD_REQUEST);
    }

//    @GetMapping("/{title}")
//    public ResponseEntity<JournalEntry> getEntryByTitle(@PathVariable String title) {
//        Optional<JournalEntry> entry = service.findEntryById(title);
//        if (entry.isPresent()) {
//            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

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
