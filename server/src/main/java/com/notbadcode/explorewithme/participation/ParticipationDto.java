package com.notbadcode.explorewithme.participation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationDto {
    Long id;

    Long event;

    Long requester;

    ParticipationStatus status;

    LocalDateTime created;
}
