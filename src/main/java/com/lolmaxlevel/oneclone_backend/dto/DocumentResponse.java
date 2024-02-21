package com.lolmaxlevel.oneclone_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DocumentResponse {
    private String name;

    private byte[] file;

    private boolean isExist;

}
