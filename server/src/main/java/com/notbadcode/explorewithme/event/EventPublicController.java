package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.dto.EventFullDto;
import com.notbadcode.explorewithme.event.dto.EventShortDto;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: События", description = "Публичный API для работы с событиями")
public class EventPublicController {
    private final EventService eventService;

    @Operation(summary = "Получение событий с возможностью фильтрации",
            description = "В выдаче только опубликованные события\n" +
                    "Текстовый поиск (по аннотации и подробному описанию) без учета регистра букв\n" +
                    "Если в запросе не указан диапазон дат, то отдает события, " +
                    "которые произойдут позже текущей даты и времени\n" +
                    "Информация о каждом событии должна включает в себя " +
                    "количество просмотров и количество одобренных заявок на участие\n" +
                    "Информация а запросе по этому эндпоинту направляется в сервис статистики")
    @ApiResponse(responseCode = "200", description = "События найдены",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EventShortDto.class)))})
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findEventsByParams(
            @RequestParam Optional<String> text,
            @RequestParam Optional<List<Long>> categories,
            @RequestParam Optional<Boolean> paid,
            @RequestParam Optional<String> rangeStart,
            @RequestParam Optional<String> rangeEnd,
            @RequestParam Optional<Boolean> onlyAvailable,
            @RequestParam Optional<String> sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /events");
        return eventService.findEventsByParams(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @Operation(summary = "Получение подробной информации об опубликованном событии",
            description = "Выводятся опубликованные события\n" +
            "Информация о событии включает в себя количество просмотров и количество подтвержденных запросов\n" +
            "Информация о том, что по этому эндпоинту был запрос, сохраняется в сервисе статистики")
    @ApiResponse(responseCode = "200", description = "Событие найдено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEventById(@PathVariable Long eventId) {
        log.info("GET /events/{}", eventId);
        return eventService.findEventById(eventId);
    }
}