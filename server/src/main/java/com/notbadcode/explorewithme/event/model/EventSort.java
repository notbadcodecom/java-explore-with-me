package com.notbadcode.explorewithme.event.model;

public enum EventSort {
    EVENT_DATE,
    VIEWS;

    public static EventSort from(String sort) {
        for (EventSort state : EventSort.values()) {
            if (state.name().equalsIgnoreCase(sort)) {
                return state;
            }
        }
        return null;
    }
}
