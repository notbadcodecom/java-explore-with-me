package com.notbadcode.explorewithme.compilation;

import com.notbadcode.explorewithme.compilation.dto.CompilationDto;
import com.notbadcode.explorewithme.util.ControllerLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: Подборки событий", description = "Публичный API для работы с подборками событий")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @Operation(summary = "Получение подборок событий")
    @ApiResponse(responseCode = "200", description = "Найдены подборки событий",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CompilationDto.class)))})
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> findCompilationByParams(
            @RequestParam Optional<Boolean> pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return compilationService.findCompilationByParams(pinned, from, size);
    }

    @Operation(summary = "Получение подробной информации об опубликованном событии")
    @ApiResponse(responseCode = "200", description = "Подборка событий найдена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompilationDto.class))})
    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto findCompilationById(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return compilationService.findCompilationById(compId);
    }
}
