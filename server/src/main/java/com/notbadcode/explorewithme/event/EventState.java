package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.error.BadRequestException;

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
        throw new BadRequestException("Wrong event state");
    }
}
