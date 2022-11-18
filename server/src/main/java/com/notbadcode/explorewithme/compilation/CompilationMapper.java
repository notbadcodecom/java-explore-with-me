package com.notbadcode.explorewithme.compilation;

import com.notbadcode.explorewithme.compilation.dto.CompilationDto;
import com.notbadcode.explorewithme.compilation.dto.NewCompilationDto;
import com.notbadcode.explorewithme.event.Event;
import com.notbadcode.explorewithme.event.EventMapper;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(Optional.ofNullable(compilationDto.getPinned()).orElse(false))
                .title(compilationDto.getTitle())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.toEventShortDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static List<CompilationDto> toCompilationDto(Page<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }
}
