package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.dto.AdminUpdateEventDto;
import com.notbadcode.explorewithme.event.dto.EventFullDto;
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
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: События", description = "API для работы с событиями")
public class EventAdminController {
    private final EventService eventService;

    @Operation(summary = "Поиск событий",
            description = "Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = EventFullDto.class)))})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findEventsByParams(
            @RequestParam Optional<List<Long>> users,
            @RequestParam Optional<List<String>> states,
            @RequestParam Optional<List<Long>> categories,
            @RequestParam Optional<String> rangeStart,
            @RequestParam Optional<String> rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /admin/events (admin searching)");
        return eventService.findEventsByParams(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Operation(summary = "Редактирование события",
            description = "Редактирование данных любого события администратором без валидации данных")
    @ApiResponse(responseCode = "200", description = "Событие отредактировано",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@RequestBody AdminUpdateEventDto eventDto, @PathVariable Long eventId) {
        log.info("PUT /admin/events/{} : {}", eventId, eventDto);
        return eventService.updateEventByAdmin(eventId, eventDto);
    }

    @Operation(summary = "Публикация события",
            description = "событие начинается после часа от даты публикации\n" +
                    "событие может быть только в ожидании публикации")
    @ApiResponse(responseCode = "200", description = "Событие опубликовано",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PatchMapping("/{eventId}/publish")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto publishEventById(@PathVariable Long eventId) {
        log.info("PATCH /admin/events/{}/publish", eventId);
        return eventService.publishEventById(eventId);
    }

    @Operation(summary = "Отклонение события")
    @ApiResponse(responseCode = "200", description = "Событие отклонено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PatchMapping("/{eventId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto rejectEventById(@PathVariable Long eventId) {
        log.info("PATCH /admin/events/{}/reject", eventId);
        return eventService.rejectEventById(eventId);
    }
}
