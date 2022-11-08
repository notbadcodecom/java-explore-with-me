package com.notbadcode.explorewithme.error;

import java.time.LocalDateTime;


public class ErrorResponse {
    private final String message;
    private final String reason;
    private final ErrorStatus status;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message, String reason, ErrorStatus status) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
