package com.notbadcode.explorewithme.participation;

import java.util.List;
import java.util.Optional;

public interface ParticipationService {
    Participation getParticipationById(Long id);

    List<ParticipationDto> findAllByParticipantId(Long userId);

    ParticipationDto createParticipation(Long userId, Optional<Long> eventIdOptional);

    ParticipationDto cancelRequestByUser(Long userId, Long requestId);

    List<ParticipationDto> findAllByInitiatorId(Long userId, Long eventId);

    ParticipationDto confirmParticipationRequest(Long userId, Long eventId, Long reqId);

    ParticipationDto rejectParticipationRequest(Long userId, Long eventId, Long reqId);
}
