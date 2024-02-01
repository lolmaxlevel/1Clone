package com.lolmaxlevel.oneclone_backend.types;

import lombok.Getter;

@Getter
public enum DocumentType {
    PASSPORT("Паспорт"),
    ID("Удостоверение личности"),
    OTHER("Иное");

    private final String name;

    DocumentType(String name) {
        this.name = name;
    }
}
