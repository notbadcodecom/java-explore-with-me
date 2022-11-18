package com.notbadcode.explorewithme.event;

public enum EventState {
    PENDING,
    PUBLISHED,
    REJECTED,
    CANCELED;

    public static EventState from(String text) {
        for (EventState state : EventState.values()) {
            if (state.name().equalsIgnoreCase(text)) {
                return state;
            }
        }
        return null;
    }
}