package com.portfolio.journalApp.repository;

import com.portfolio.journalApp.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface JournalRepository extends MongoRepository<JournalEntry, String> {
}
