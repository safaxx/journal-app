package com.portfolio.journalApp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String username;

    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Hide password in JSON responses
    private String password;

    private String email;

    private LocalDateTime createdDate;

    private LocalDateTime lastLoginDate;

    private List<String> roles;

    //creating reference of journalEntry in users collection
    @DBRef
    @JsonManagedReference
    private ArrayList<JournalEntry> entries = new ArrayList<>();

    // Constructor for backward compatibility
    public User(String id, String username, String password, List<String> roles, ArrayList<JournalEntry> entries) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.entries = entries;
        this.createdDate = LocalDateTime.now();
    }
}