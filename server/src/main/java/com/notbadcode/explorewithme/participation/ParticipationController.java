package com.notbadcode.explorewithme.participation;

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
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private: Запросы на участие", description = "Закрытый API для работы с запросами на участие в событиях")
public class ParticipationController {
    private final ParticipationService participationService;

    @Operation(summary = "Получение информации о заявках текущего пользователя на участие в чужих событиях")
    @ApiResponse(responseCode = "200", description = "Найдены запросы на участие",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ParticipationDto.class)))})
    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationDto> findAllByParticipantId(@PathVariable Long userId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return participationService.findAllByParticipantId(userId);
    }

    @Operation(summary = "Отмена своего запроса на участие в событии")
    @ApiResponse(responseCode = "200", description = "Заявка отменена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ParticipationDto.class))})
    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationDto cancelRequestByUser(
            @PathVariable Long userId,
            @PathVariable Long requestId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return participationService.cancelRequestByUser(userId, requestId);
    }

    @Operation(summary = "Добавление запроса от текущего пользователя на участие в событии",
            description = "нельзя добавить повторный запрос,\n" +
                    "инициатор события не может добавить запрос на участие в своём событии,\n" +
                    "нельзя участвовать в неопубликованном событии,\n" +
                    "если у события достигнут лимит запросов на участие, вовращается ошибка\n" +
                    "если для события отключена модерация запросов на участие, то запрос автоматически подтверждается")
    @ApiResponse(responseCode = "200", description = "Заявка создана",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ParticipationDto.class))})
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationDto createRequest(
            @PathVariable Long userId,
            @RequestParam Optional<Long> eventId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return participationService.createParticipation(userId, eventId);
    }

    @Operation(summary = "Получение информации о запросах на участие в событии текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Найдены запросы на участие",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ParticipationDto.class)))})
    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationDto> findAllByInitiatorId(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return participationService.findAllByInitiatorId(userId, eventId);
    }

    @Operation(summary = "Подтверждение чужой заявки на участие в событии текущего пользователя",
            description = "Если для события лимит заявок равен 0 или отключена пре-модерация заявок, " +
                    "то подтверждение заявок не требуется\n" +
                    "нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие\n" +
                    "если при подтверждении данной заявки, лимит заявок для события исчерпан, " +
                    "то все неподтверждённые заявки необходимо отклонить")
    @ApiResponse(responseCode = "200", description = "Заявка подтверждена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ParticipationDto.class))})
    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationDto confirmParticipationRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return participationService.confirmParticipationRequest(userId, eventId, reqId);
    }

    @Operation(summary = "Отклонение чужой заявки на участие в событии текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Заявка отклонена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ParticipationDto.class))})
    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationDto rejectParticipationRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return participationService.rejectParticipationRequest(userId, eventId, reqId);
    }
}
