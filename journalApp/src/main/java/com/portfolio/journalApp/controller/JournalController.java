package com.portfolio.journalApp.controller;

import com.portfolio.journalApp.dto.ResponseDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.exceptions.ResourceNotFoundException;
import com.portfolio.journalApp.service.JournalService;
import com.portfolio.journalApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService service;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllJournalEntriesOfUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            List<JournalEntry> allEntries = service.getAllEntries(userDetails.getUsername(), sortOrder);
            if (allEntries != null && !allEntries.isEmpty()) {
                return new ResponseEntity<>(
                        new ResponseDTO("Entries retrieved successfully", allEntries),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    new ResponseDTO(false, "No entries found for: " + userDetails.getUsername()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Error retrieving entries: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @GetMapping("/paginated")
    public ResponseEntity<ResponseDTO> getPaginatedEntries(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            Page<JournalEntry> entries = service.getPaginatedEntries(userDetails.getUsername(), page, size, sortOrder);
            return new ResponseEntity<>(
                    new ResponseDTO("Paginated entries retrieved successfully", entries),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO("Error retrieving paginated entries: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{entryId}")
    public ResponseEntity<ResponseDTO> getEntryById(
            @PathVariable String entryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<JournalEntry> entry = service.findEntryByIdAndUser(entryId, userDetails.getUsername());
            if (entry.isPresent()) {
                return new ResponseEntity<>(
                        new ResponseDTO("Entry retrieved successfully", entry.get()),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Entry not found"),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Error retrieving entry: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchEntries(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<JournalEntry> entries = service.searchEntries(userDetails.getUsername(), query);
            return new ResponseEntity<>(
                    new ResponseDTO("Search completed", entries),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Error searching entries: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/filter/date-range")
    public ResponseEntity<ResponseDTO> getEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<JournalEntry> entries = service.getEntriesByDateRange(
                    userDetails.getUsername(),
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59)
            );
            return new ResponseEntity<>(
                    new ResponseDTO("Entries filtered successfully", entries),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Error filtering entries: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

//    @GetMapping("/recent")
//    public ResponseEntity<ResponseDTO> getRecentEntries(
//            @RequestParam(defaultValue = "7") int days,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            LocalDateTime since = LocalDateTime.now().minusDays(days);
//            List<JournalEntry> entries = service.getEntriesSince(userDetails.getUsername(), since);
//            return new ResponseEntity<>(
//                    new ResponseDTO("Recent entries retrieved successfully", entries),
//                    HttpStatus.OK
//            );
//        } catch (Exception e) {
//            return new ResponseEntity<>(
//                    new ResponseDTO(false, "Error retrieving recent entries: " + e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
//
//
//    @GetMapping("/stats")
//    public ResponseEntity<ResponseDTO> getJournalStats(@AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            var stats = service.getJournalStats(userDetails.getUsername());
//            return new ResponseEntity<>(
//                    new ResponseDTO("Statistics retrieved successfully", stats),
//                    HttpStatus.OK
//            );
//        } catch (Exception e) {
//            return new ResponseEntity<>(
//                    new ResponseDTO("Error retrieving statistics: " + e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }

    // 8. Create entry (enhanced with validation)
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createEntryForUser(
            @Valid @RequestBody JournalEntry entry,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            JournalEntry newEntry = service.saveEntry(entry, userDetails.getUsername());
            if (newEntry != null) {
                return new ResponseEntity<>(
                        new ResponseDTO("New journal entry created", newEntry),
                        HttpStatus.CREATED
                );
            }
            return new ResponseEntity<>(
                    new ResponseDTO(false, "New journal entry creation failed"),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Error creating entry: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/update/{entryId}")
    public ResponseEntity<ResponseDTO> updateEntryById(
            @PathVariable String entryId,
            @Valid @RequestBody JournalEntry newEntry,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            JournalEntry updatedEntry = service.updateEntry(entryId, newEntry, userDetails.getUsername());
            if (updatedEntry != null) {
                return new ResponseEntity<>(
                        new ResponseDTO("Entry updated successfully", updatedEntry),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    new ResponseDTO(false, "Entry not found or access denied"),
                    HttpStatus.NOT_FOUND
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new ResponseDTO(false, e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO("Error updating entry: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @DeleteMapping("/delete/{entryId}")
    public ResponseEntity<ResponseDTO> deleteEntryOfUser(
            @PathVariable String entryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            boolean deleted = service.deleteEntry(entryId, userDetails.getUsername());
            if (deleted) {
                return new ResponseEntity<>(
                        new ResponseDTO("Entry deleted successfully"),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    new ResponseDTO("Entry not found or access denied"),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO("Error deleting entry: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}