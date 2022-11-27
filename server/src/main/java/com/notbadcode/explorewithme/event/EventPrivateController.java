package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.dto.EventFullDto;
import com.notbadcode.explorewithme.event.dto.EventShortDto;
import com.notbadcode.explorewithme.event.dto.NewEventDto;
import com.notbadcode.explorewithme.event.dto.UpdateEventDto;
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
    @ApiResponse(responseCode = "200", description = "Событие добавлено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto createEvent(
            @Valid @RequestBody NewEventDto eventDto,
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
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
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventService.updateEventByUser(eventDto, userId);
    }

    @Operation(summary = "Получение полной информации о событии добавленном текущим пользователем")
    @ApiResponse(responseCode = "200", description = "Событие найдено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEventById(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventService.findEventByInitiatorById(eventId, userId);
    }

    @Operation(summary = "Получение событий, добавленных текущим пользователем")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = EventFullDto.class)))})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findEventsByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventService.findEventsByUserId(userId, from, size);
    }

    @Operation(summary = "Отмена события добавленного текущим пользователем",
            description = "Отменить можно только событие в состоянии ожидания модерации")
    @ApiResponse(responseCode = "200", description = "Событие обновлено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto cancelEventById(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventService.cancelEventById(eventId, userId);
    }

    @Operation(summary = "Добавление нового события с проверкой попадания в локацию")
    @ApiResponse(responseCode = "200", description = "Событие добавлено",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EventFullDto.class))})
    @PostMapping("/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto createEventInLocation(
            @Valid @RequestBody NewEventDto eventDto,
            @PathVariable Long userId,
            @PathVariable Long locationId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return eventService.createEventInLocation(eventDto, userId, locationId);
    }
}
