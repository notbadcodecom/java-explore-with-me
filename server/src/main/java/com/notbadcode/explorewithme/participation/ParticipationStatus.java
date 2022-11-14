package com.notbadcode.explorewithme.participation;

public enum ParticipationStatus {
    PENDING,
    CANCELED,
    CONFIRMED,
    REJECTED;

    public static ParticipationStatus from(String text) {
        for (ParticipationStatus state : ParticipationStatus.values()) {
            if (state.name().equalsIgnoreCase(text)) {
                return state;
            }
        }
        return null;
    }
}
