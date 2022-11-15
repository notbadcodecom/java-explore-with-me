package com.notbadcode.explorewithme.compilation.dto;

import com.notbadcode.explorewithme.event.dto.EventShortDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;

    List<EventShortDto> events;

    Boolean pinned;

    String title;
}
