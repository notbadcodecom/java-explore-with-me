package com.notbadcode.explorewithme.compilation;

import com.notbadcode.explorewithme.compilation.dto.CompilationDto;
import com.notbadcode.explorewithme.compilation.dto.NewCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    Compilation getCompilationOr404Error(Long compId);

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void deletePinOfCompilation(Long compId);

    void setPinOfCompilation(Long compId);

    List<CompilationDto> findCompilationByParams(
            Optional<Boolean> pinned,
            int from,
            int size
    );

    CompilationDto findCompilationById(Long compId);
}
