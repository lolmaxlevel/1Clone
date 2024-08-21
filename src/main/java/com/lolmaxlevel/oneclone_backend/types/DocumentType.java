package com.lolmaxlevel.oneclone_backend.types;

import lombok.Getter;

@Getter
public enum DocumentType {
    PASSPORT("Паспорт"),
    ID("Удостовер. Личности"),
    OTHER("Иное");

    private final String name;

    DocumentType(String name) {
        this.name = name;
    }
}
