package com.lolmaxlevel.oneclone_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadResponse {
    private String message;
    private String name;
    private Long id;

    public UploadResponse(String message) {
        this.message = message;
    }
}

