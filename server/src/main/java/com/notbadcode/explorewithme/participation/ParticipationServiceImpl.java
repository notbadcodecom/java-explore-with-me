package com.notbadcode.explorewithme.participation;

import com.notbadcode.explorewithme.error.BadRequestException;
import com.notbadcode.explorewithme.error.ForbiddenException;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.event.Event;
import com.notbadcode.explorewithme.event.EventService;
import com.notbadcode.explorewithme.event.EventState;
import com.notbadcode.explorewithme.user.User;
import com.notbadcode.explorewithme.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public Participation getParticipationOr404Error(Long id) {
        log.debug("Load participation id={}", id);
        return participationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Participation with id=" + id + " was not found"));
    }

    @Override
    public List<ParticipationDto> findAllByParticipantId(Long userId) {
        List<Participation> requests = participationRepository.findByParticipant_Id(userId);
        log.debug("{} participation requests were found", requests.size());
        return ParticipationMapper.toParticipationDto(requests);
    }

    @Override
    @Transactional
    public ParticipationDto createParticipation(Long userId, Optional<Long> eventIdOptional) {
        Event event = eventService.getEventOr404Error(
                eventIdOptional.orElseThrow(() -> new BadRequestException("EventId is not in query"))
        );
        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Forbidden to create a request from initiator");
        }
        if (participationRepository.existsByEvent_IdAndParticipant_Id(event.getId(), userId)) {
            throw new BadRequestException("Request already exist");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event is not published");
        }
        if (event.getParticipantLimit() <= event.getParticipants().size()) {
            throw new ForbiddenException("Limit of participants is exhausted");
        }
        Participation request = participationRepository.save(Participation.builder()
                .participant(userService.getUserOr404Error(userId))
                .status((event.getModeration()) ? ParticipationStatus.PENDING : ParticipationStatus.CONFIRMED)
                .event(event)
                .build());
        log.debug("Created participation requests id={}", request.getId());
        return ParticipationMapper.toParticipationDto(request);
    }

    @Override
    @Transactional
    public ParticipationDto cancelRequestByUser(Long userId, Long requestId) {
        User user = userService.getUserOr404Error(userId);
        Participation participation = getParticipationOr404Error(requestId);
        if (!participation.getParticipant().getId().equals(user.getId())) {
            throw new ForbiddenException("Forbidden to cancel request of another user");
        }
        if (!participation.getStatus().equals(ParticipationStatus.PENDING)) {
            throw new BadRequestException("Request is not in pending status");
        }
        participation.setStatus(ParticipationStatus.CANCELED);
        return ParticipationMapper.toParticipationDto(participationRepository.save(participation));
    }

    @Override
    public List<ParticipationDto> findAllByInitiatorId(Long userId, Long eventId) {
        List<Participation> requests = participationRepository.findByEvent_Initiator_IdAndEvent_Id(userId, eventId);
        log.debug("Find {} participation requests", requests.size());
        return ParticipationMapper.toParticipationDto(requests);
    }

    @Override
    @Transactional
    public ParticipationDto confirmParticipationRequest(Long userId, Long eventId, Long reqId) {
        User initiator = userService.getUserOr404Error(userId);
        Event event = eventService.getEventOr404Error(eventId);
        Participation participation = getParticipationOr404Error(reqId);
        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new ForbiddenException("Forbidden to confirm request by another user");
        }
        if (event.getParticipantLimit() == 0 || !event.getModeration()) {
            throw new BadRequestException("Request already confirmed");
        }
        if (event.getParticipantLimit() == event.getParticipants().size()) {
            throw new ForbiddenException("Limit of participants is exhausted");
        } else if (event.getParticipantLimit() - event.getParticipants().size() == 1) {
            participationRepository.saveAll(participationRepository
                    .findByEvent_IdAndStatus(eventId, ParticipationStatus.PENDING).stream()
                    .peek(e -> e.setStatus(ParticipationStatus.CANCELED))
                    .collect(Collectors.toList()));
        }
        participation.setStatus(ParticipationStatus.CONFIRMED);
        return ParticipationMapper.toParticipationDto(participationRepository.save(participation));
    }

    @Override
    @Transactional
    public ParticipationDto rejectParticipationRequest(Long userId, Long eventId, Long reqId) {
        User initiator = userService.getUserOr404Error(userId);
        Event event = eventService.getEventOr404Error(eventId);
        Participation participation = getParticipationOr404Error(reqId);
        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new ForbiddenException("Forbidden to reject request by another user");
        }
        if (event.getParticipantLimit() == 0 || !event.getModeration()) {
            throw new BadRequestException("Rejecting is not required");
        }
        participation.setStatus(ParticipationStatus.REJECTED);
        return ParticipationMapper.toParticipationDto(participationRepository.save(participation));
    }
}
