package com.example.demo.dto.heart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
public class HeartResponse {
    private boolean isAdded;
    private String message;
    private HeartDto heartDto;

    public HeartResponse(boolean isAdded, String message) {
        this.isAdded = isAdded;
        this.message = message;
    }
}