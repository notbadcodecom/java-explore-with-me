package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.error.BadRequestException;

public enum EventSort {
    EVENT_DATE,
    VIEWS;

    public static EventSort from(String sort) {
        for (EventSort state : EventSort.values()) {
            if (state.name().equalsIgnoreCase(sort)) {
                return state;
            }
        }
        throw new BadRequestException("No field for sorting");
    }
}
