package com.lolmaxlevel.oneclone_backend.types;

import lombok.Getter;

@Getter
public enum WorkPositionType {
    maid("Горничная"),
    houseman("Хаусмен"),
    lobby("Лоббист"),
    supervisor("Супервайзер"),
    coordinator("Координатор"),
    laundry("Прачка"),
    cook("Повар");

    private final String russianName;

    WorkPositionType(String russianName) {
        this.russianName = russianName;
    }


}
