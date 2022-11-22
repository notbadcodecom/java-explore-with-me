package com.notbadcode.explorewithme.compilation.dto;

import com.notbadcode.explorewithme.event.dto.EventShortDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    private String title;
}
