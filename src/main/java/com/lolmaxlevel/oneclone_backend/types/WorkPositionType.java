package com.lolmaxlevel.oneclone_backend.types;

import lombok.Getter;

@Getter
public enum WorkPositionType {
    maid("Горничная"),
    houseman("Хаусмен"),
    lobby("Лоббист"),
    supervisor("Супервайзер");

    private final String russianName;

    WorkPositionType(String russianName) {
        this.russianName = russianName;
    }


}
