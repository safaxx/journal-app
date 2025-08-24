package com.portfolio.journalApp.service;

import com.portfolio.journalApp.dto.JournalEntryDTO;
import com.portfolio.journalApp.dto.UserJournalEntryDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.exceptions.ResourceNotFoundException;
import com.portfolio.journalApp.repository.JournalRepository;
import com.portfolio.journalApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public JournalEntry saveEntry(JournalEntry entry, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        }
        entry.setCreatedDate(LocalDateTime.now());
        entry.setUser(user);
        JournalEntry savedEntry = repository.save(entry);

        user.getEntries().add(savedEntry);
        userRepository.save(user);
        return savedEntry;
    }

    public List<JournalEntry> getAllEntries(String username) {
        User user = userRepository.findByUsername(username);
        return user.getEntries();
    }

    public  List<UserJournalEntryDTO> getAllEntriesForAdmin() {
        List<JournalEntry> all = repository.findAll();
        Map<String, List<JournalEntry>> userJournalMap = new HashMap<>();
        for(JournalEntry e: all){
            userJournalMap
                    .computeIfAbsent(e.getUser().getUsername(), k -> new ArrayList<>())
                    .add(e);
        }
        List<UserJournalEntryDTO> list = new ArrayList<>();
        for(Map.Entry<String, List<JournalEntry>> entry: userJournalMap.entrySet()){
            UserJournalEntryDTO dto = new UserJournalEntryDTO();
            dto.setUsername(entry.getKey());
            ArrayList<JournalEntryDTO> dtoList = new ArrayList<>();
            for(JournalEntry je: entry.getValue()){
                JournalEntryDTO jeDto = new JournalEntryDTO();
                jeDto.setCreatedDate(je.getCreatedDate());
                jeDto.setTitle(je.getTitle());
                jeDto.setContent(je.getContent());
                dtoList.add(jeDto);
            }
            dto.setJournalEntries(dtoList);
            list.add(dto);
        }
        return list;
    }

    public Optional<JournalEntry> findEntryById(String id) {
        return repository.findById(id);
    }

    public JournalEntry updateEntry(String entryId, JournalEntry newEntry) throws ResourceNotFoundException {
        Optional<JournalEntry> oldEntry = repository.findById(entryId);
        if (oldEntry.isPresent()) {
            JournalEntry existingEntry = oldEntry.get();
            existingEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : existingEntry.getTitle());
            existingEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : existingEntry.getContent());
            existingEntry.setCreatedDate(LocalDateTime.now());
            return repository.save(existingEntry);
        } else {
            throw new ResourceNotFoundException("Entry with this id doesn't exist");
        }
    }

    @Transactional
    public void deleteEntry(String id, String username) {
        User user = userRepository.findByUsername(username);
        user.getEntries().removeIf(entry -> entry.getId().equalsIgnoreCase(id));
        userRepository.save(user);
        repository.deleteById(id);

    }
}
