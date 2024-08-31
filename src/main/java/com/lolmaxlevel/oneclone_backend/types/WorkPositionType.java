package com.lolmaxlevel.oneclone_backend.types;

import lombok.Getter;

@Getter
public enum WorkPositionType {
    maid("Горничная"),
    houseman("Хаусмен"),
    janitor("Дворник"),
    lobby("Лоббист"),
    supervisor("Супервайзер"),
    coordinator("Координатор"),
    laundry("Прачка"),
    cook("Повар"),
    waiter("Официант"),
    bellboy("Беллман"),
    cleaner("Уборщик"),
    ;

    private final String russianName;

    WorkPositionType(String russianName) {
        this.russianName = russianName;
    }


    }
