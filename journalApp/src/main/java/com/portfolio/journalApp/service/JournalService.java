package com.portfolio.journalApp.service;

import com.portfolio.journalApp.dto.JournalEntryDTO;
import com.portfolio.journalApp.dto.UserJournalEntryDTO;
import com.portfolio.journalApp.entity.JournalEntry;
import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.exceptions.ResourceNotFoundException;
import com.portfolio.journalApp.repository.JournalRepository;
import com.portfolio.journalApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getEntries();
    }

    public List<JournalEntry> getAllEntries(String username, String sortOrder) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<JournalEntry> entries = new ArrayList<>(user.getEntries());
        
        if ("asc".equalsIgnoreCase(sortOrder)) {
            entries.sort(Comparator.comparing(JournalEntry::getCreatedDate));
        } else {
            entries.sort(Comparator.comparing(JournalEntry::getCreatedDate).reversed());
        }
        
        return entries;
    }

    public Page<JournalEntry> getPaginatedEntries(String username, int page, int size, String sortOrder) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Page.empty();
        }

        Sort sort = "asc".equalsIgnoreCase(sortOrder) ? 
            Sort.by("createdDate").ascending() : 
            Sort.by("createdDate").descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        // For MongoDB, we'll need to implement custom pagination since we're getting entries from user
        List<JournalEntry> allEntries = getAllEntries(username, sortOrder);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allEntries.size());
        
        List<JournalEntry> pageContent = start < allEntries.size() ? 
            allEntries.subList(start, end) : new ArrayList<>();
            
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allEntries.size());
    }

    public Optional<JournalEntry> findEntryByIdAndUser(String entryId, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        
        return user.getEntries().stream()
            .filter(entry -> entry.getId().equals(entryId))
            .findFirst();
    }

    public List<JournalEntry> searchEntries(String username, String query) {
        User user = userRepository.findByUsername(username);
        if (user == null || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerQuery = query.toLowerCase();
        return user.getEntries().stream()
            .filter(entry -> 
                (entry.getTitle() != null && entry.getTitle().toLowerCase().contains(lowerQuery)) ||
                (entry.getContent() != null && entry.getContent().toLowerCase().contains(lowerQuery))
            )
            .sorted(Comparator.comparing(JournalEntry::getCreatedDate).reversed())
            .collect(Collectors.toList());
    }

    public List<JournalEntry> getEntriesByDateRange(String username, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        
        return user.getEntries().stream()
            .filter(entry -> 
                entry.getCreatedDate() != null &&
                !entry.getCreatedDate().isBefore(startDate) &&
                !entry.getCreatedDate().isAfter(endDate)
            )
            .sorted(Comparator.comparing(JournalEntry::getCreatedDate).reversed())
            .collect(Collectors.toList());
    }

    public List<JournalEntry> getEntriesSince(String username, LocalDateTime since) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        
        return user.getEntries().stream()
            .filter(entry -> 
                entry.getCreatedDate() != null &&
                entry.getCreatedDate().isAfter(since)
            )
            .sorted(Comparator.comparing(JournalEntry::getCreatedDate).reversed())
            .collect(Collectors.toList());
    }

    /*public Map<String, Object> getJournalStats(String username) {
        User user = userRepository.findByUsername(username);
        Map<String, Object> stats = new HashMap<>();
        
        if (user == null || user.getEntries() == null) {
            stats.put("totalEntries", 0);
            stats.put("entriesThisMonth", 0);
            stats.put("entriesThisWeek", 0);
            stats.put("averageEntriesPerWeek", 0.0);
            stats.put("longestStreak", 0);
            return stats;
        }
        
        List<JournalEntry> entries = user.getEntries();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        LocalDateTime monthAgo = now.minusDays(30);
        
        long totalEntries = entries.size();
        long entriesThisWeek = entries.stream()
            .filter(e -> e.getCreatedDate() != null && e.getCreatedDate().isAfter(weekAgo))
            .count();
        long entriesThisMonth = entries.stream()
            .filter(e -> e.getCreatedDate() != null && e.getCreatedDate().isAfter(monthAgo))
            .count();
            
        double averageEntriesPerWeek = totalEntries > 0 ? (double) totalEntries / 4.0 : 0.0; // Rough estimate
        
        stats.put("totalEntries", totalEntries);
        stats.put("entriesThisMonth", entriesThisMonth);
        stats.put("entriesThisWeek", entriesThisWeek);
        stats.put("averageEntriesPerWeek", Math.round(averageEntriesPerWeek * 100.0) / 100.0);
        stats.put("firstEntryDate", entries.stream()
            .map(JournalEntry::getCreatedDate)
            .filter(Objects::nonNull)
            .min(LocalDateTime::compareTo)
            .orElse(null));
        stats.put("lastEntryDate", entries.stream()
            .map(JournalEntry::getCreatedDate)
            .filter(Objects::nonNull)
            .max(LocalDateTime::compareTo)
            .orElse(null));
            
        return stats;
    }*/

    public List<UserJournalEntryDTO> getAllEntriesForAdmin() {
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

    public JournalEntry updateEntry(String entryId, JournalEntry newEntry, String username) throws ResourceNotFoundException {
        Optional<JournalEntry> entryOpt = findEntryByIdAndUser(entryId, username);
        if (entryOpt.isEmpty()) {
            throw new ResourceNotFoundException("Entry not found or access denied");
        }
        
        JournalEntry existingEntry = entryOpt.get();
        existingEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? 
            newEntry.getTitle() : existingEntry.getTitle());
        existingEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? 
            newEntry.getContent() : existingEntry.getContent());

        return repository.save(existingEntry);
    }


    @Transactional
    public boolean deleteEntry(String id, String username) {
        Optional<JournalEntry> entryOpt = findEntryByIdAndUser(id, username);
        if (entryOpt.isEmpty()) {
            return false;
        }
        
        User user = userRepository.findByUsername(username);
        user.getEntries().removeIf(entry -> entry.getId().equals(id));
        userRepository.save(user);
        repository.deleteById(id);
        return true;
    }

}