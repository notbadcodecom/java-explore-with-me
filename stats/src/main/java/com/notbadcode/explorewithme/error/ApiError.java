package com.notbadcode.explorewithme.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder.Default
    final List<String> errors = null;

    final String message;

    final String reason;

    final ErrorStatus status;

    @Builder.Default
    final LocalDateTime timestamp = LocalDateTime.now();
}
