package com.portfolio.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    private String message;
    private Object data;

    public ResponseDTO(String message){
        this.message = message;
    }
}
