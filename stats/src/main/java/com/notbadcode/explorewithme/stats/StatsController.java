package com.notbadcode.explorewithme.stats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
@Tag(name = "StatsController", description = "API для работы со статистикой посещений")
public class StatsController {
    private final StatsService service;

    @Operation(summary = "Сохранение информации о том, что к эндпоинту был запрос",
            description = "Сохранение информации о том, что на uri конкретного сервиса " +
                    "был отправлен запрос пользователем. Название сервиса, uri и ip " +
                    "пользователя указаны в теле запроса")
    @ApiResponse(responseCode = "200")
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.OK)
    public void saveHit(@Valid @RequestBody EndpointHit hit) {
        log.info("PUT /hit : {}", hit.getUri());
        service.saveHit(hit);
   }

    @Operation(summary = "Получение статистики по посещениям")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ViewStats.class)))})
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(
            @Parameter(description = "Дата и время начала диапазона за который нужно выгрузить " +
                    "статистику (в формате \"yyyy-MM-dd HH:mm:ss\")") @RequestParam String start,
            @Parameter(description = "Дата и время конца диапазона за который нужно выгрузить " +
                    "статистику (в формате \"yyyy-MM-dd HH:mm:ss\")") @RequestParam String end,
            @Parameter(description = "Список uri для которых нужно выгрузить статистику")
            @RequestParam Optional<List<String>> uris,
            @Parameter(description = "Нужно ли учитывать только уникальные посещения " +
                    "(только с уникальным ip)") @RequestParam(defaultValue = "false") boolean unique
    ) {
        StringBuilder builder = new StringBuilder("GET /stats?start=" + start + "&end=" + end);
        uris.ifPresent(u -> u.forEach(q -> builder.append("&uris=").append(q)));
        builder.append("&unique=").append(unique);
        log.info("{}", builder);
        return service.getStats(start, end, uris, unique);
    }
}
