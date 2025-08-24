package com.portfolio.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JournalEntryDTO {
    private String title;
    private String content;
    private LocalDateTime createdDate;

}
