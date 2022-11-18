package com.notbadcode.explorewithme.compilation;

import com.notbadcode.explorewithme.compilation.dto.CompilationDto;
import com.notbadcode.explorewithme.compilation.dto.NewCompilationDto;
import com.notbadcode.explorewithme.util.ControllerLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Подборки событий", description = "API для работы с подборками событий")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @Operation(summary = "Добавление новой подборки")
    @ApiResponse(responseCode = "200", description = "Подборка добавлена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompilationDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto createUser(
            @Valid @RequestBody NewCompilationDto compilationDto,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return compilationService.createCompilation(compilationDto);
    }

    @Operation(summary = "Удаление подборки")
    @ApiResponse(responseCode = "200", description = "Подборка удалена")
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCompilation(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        compilationService.deleteCompilation(compId);
    }

    @Operation(summary = "Удалить событие из подборки")
    @ApiResponse(responseCode = "200", description = "Событие удалено из подборки")
    @DeleteMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEventFromCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @Operation(summary = "Добавить событие в подборку")
    @ApiResponse(responseCode = "200", description = "Событие добавлено")
    @PatchMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void addEventToCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        compilationService.addEventToCompilation(compId, eventId);
    }

    @Operation(summary = "Открепить подборку на главной странице")
    @ApiResponse(responseCode = "200", description = "Подборка откреплена")
    @DeleteMapping("/{compId}/pin")
    @ResponseStatus(HttpStatus.OK)
    public void deletePinOfCompilation(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        compilationService.deletePinOfCompilation(compId);
    }

    @Operation(summary = "Закрепить подборку на главной странице")
    @ApiResponse(responseCode = "200", description = "Подборка закреплена")
    @PatchMapping("/{compId}/pin")
    @ResponseStatus(HttpStatus.OK)
    public void setPinOfCompilation(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        compilationService.setPinOfCompilation(compId);
    }
}
