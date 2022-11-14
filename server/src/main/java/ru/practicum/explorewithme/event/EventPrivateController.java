package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.dto.UpdateEventDto;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private: События", description = "Закрытый API для работы с событиями")
public class EventPrivateController {
    private final EventService eventService;

    @Operation(summary = "Добавление нового события",
            description = "Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента")
    @ApiResponse(responseCode = "201", description = "Событие добавлено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @Valid @RequestBody NewEventDto eventDto,
            @PathVariable Long userId
    ) {
        log.info("POST /users/{}/events : {}", userId, eventDto);
        return eventService.createEvent(eventDto, userId);
    }

    @Operation(summary = "Изменение события добавленного текущим пользователем",
            description = "Изменить можно только отмененные события или события в состоянии ожидания модерации\n" +
                    "Если редактируется отменённое событие, то оно автоматически " +
                    "переходит в состояние ожидания модерации\n" +
                    "Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента")
    @ApiResponse(responseCode = "200", description = "Событие обновлено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @Valid @RequestBody UpdateEventDto eventDto,
            @PathVariable Long userId
    ) {
        log.info("PATCH /users/{}/events : {}", userId, eventDto);
        return eventService.updateEventByUser(eventDto, userId);
    }

    @Operation(summary = "Получение полной информации о событии добавленном текущим пользователем")
    @ApiResponse(responseCode = "200", description = "Событие найдено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.findEventById(eventId, userId);
    }

    @Operation(summary = "Получение событий, добавленных текущим пользователем")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = EventFullDto.class)))})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findEventsByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        log.info("GET /users/{}/events", userId);
        return eventService.findEventsByUserId(userId, from, size);
    }

    @Operation(summary = "Отмена события добавленного текущим пользователем",
            description = "Отменить можно только событие в состоянии ожидания модерации")
    @ApiResponse(responseCode = "200", description = "Событие обновлено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto cancelEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("PATCH /users/{}/events/{}", userId, eventId);
        return eventService.cancelEventById(eventId, userId);
    }
}
