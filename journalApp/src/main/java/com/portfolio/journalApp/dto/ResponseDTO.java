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

    private boolean success;
    private String message;
    private Object data;

    public ResponseDTO(String message){
        this.message = message;
    }
    public ResponseDTO(boolean success, String message){
        this.message = message;
        this.success = success;
    }

    public ResponseDTO(boolean success, Object data){
        this.data = data;
        this.success = success;
    }

    public ResponseDTO(String message, Object data){
        this.message = message;
        this.data = data;
    }
}
