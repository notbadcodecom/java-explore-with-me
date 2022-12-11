package com.notbadcode.explorewithme.location;

import com.notbadcode.explorewithme.error.BadRequestException;

public enum LocationType {
    // Город
    CITY,
    // Театр
    THEATER,
    // Спортивный объект
    SPORTS,
    // Концертный зал
    CONCERT_HALL,
    // Цирк
    CIRCUS,
    // Клуб
    CLUB,
    // Открытое место
    OPEN_AIR,
    // Ресторан, бар и т.п
    FOOD_PLACES;

    public static LocationType from(String text) {
        for (LocationType type : LocationType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new BadRequestException("Wrong type of location ");
    }
}
