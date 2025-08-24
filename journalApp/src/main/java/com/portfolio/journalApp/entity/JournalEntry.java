package com.portfolio.journalApp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

//@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "journal_entries")
public class JournalEntry {

    @Id
    private String id;

    @NonNull
    private String title;
    private String content;
    private LocalDateTime createdDate;

    @DBRef
    @JsonBackReference
    private User user;
}
