package com.lolmaxlevel.oneclone_backend.types;

import lombok.Getter;

@Getter
public enum CountryType {
    kg("Киргизская Республика", "Кыргызстан", "Киргизской Республики"),
    ru("Российская Федерация", "Россия", "Российской Федерации"),
    ;

    private final String officialName;
    private final String name;
    private final String genitiveName;

    CountryType(String officialName, String name, String genitiveName) {
        this.officialName = officialName;
        this.name = name;
        this.genitiveName = genitiveName;
    }

}
