package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.category.CategoryService;
import com.notbadcode.explorewithme.error.BadRequestException;
import com.notbadcode.explorewithme.error.ForbiddenException;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.event.dto.*;
import com.notbadcode.explorewithme.event.model.*;
import com.notbadcode.explorewithme.user.User;
import com.notbadcode.explorewithme.user.UserService;
import com.notbadcode.explorewithme.util.SizeRequest;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    @Value("${spring.jackson.date-format}") private String format;

    @Transactional
    public EventFullDto createEvent(NewEventDto eventDto, Long userId) {
        Event event = EventMapper.toEvent(eventDto);
        event.setInitiator(userService.getUserOr404Error(userId));
        event.setCategory(categoryService.getCategoryOr404Error(eventDto.getCategory()));
        event.setLocation(createLocation(eventDto.getLocation()));
        event = eventRepository.save(event);
        log.debug("Event id={} has been created", event.getId());
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    public Location createLocation(LocationDto locationDto) {
        Location location = locationRepository.save(EventMapper.toLocation(locationDto));
        log.debug("Location id={} has been created", location.getId());
        return location;
    }

    public boolean existsByCategoryId(Long categoryId) {
        boolean exist = eventRepository.existsByCategory_Id(categoryId);
        log.debug("Using category id={}: {}", categoryId, exist);
        return exist;
    }

    public EventFullDto findEventById(Long eventId, Long userId) {
        User user = userService.getUserOr404Error(userId);
        Event event = getEventOr404Error(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Requesting another user's event");
        }
        log.debug("Find event id={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    public EventFullDto findEventById(Long eventId) {
        Event event = getEventOr404Error(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not published");
        }
        log.debug("Find event id={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    public Event getEventOr404Error(Long eventId) {
        log.debug("Load event id={}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    public List<EventShortDto> findEventsByUserId(Long userId, int from, int size) {
        userService.getUserOr404Error(userId);
        Pageable pageable = SizeRequest.from(from, size);
        List<Event> events = eventRepository.findByInitiator_IdOrderByEventDateDesc(userId, pageable);
        log.debug("Find {} events", events.size());
        return EventMapper.toEventShortDto(events);
    }

    @Transactional
    public EventFullDto updateEventByUser(UpdateEventDto eventDto, Long userId) {
        User user = userService.getUserOr404Error(userId);
        Event event = getEventOr404Error(eventDto.getEventId());
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Updating another user's event");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Event id={" + eventDto.getEventId() + "} is not pending moderation");
        }
        event.setState(EventState.PENDING);
        return EventMapper.toEventFullDto(updateEvent(event, eventDto));
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventDto eventDto) {
        Event event = getEventOr404Error(eventId);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setModeration);
        Optional.ofNullable(eventDto.getLocation())
                .ifPresent(locationDto -> event.setLocation(EventMapper.toLocation(locationDto)));
        return EventMapper.toEventFullDto(updateEvent(event, eventDto));
    }

    private Event updateEvent(Event event, Object object) {
        UpdateEventDto eventDto = (UpdateEventDto) object;
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getCategory())
                .ifPresent(e -> event.setCategory(categoryService.getCategoryOr404Error(e)));
        eventRepository.save(event);
        log.debug("Event id={} has been updated", event.getId());
        return event;
    }

    @Transactional
    public EventFullDto cancelEventById(Long eventId, Long userId) {
        User user = userService.getUserOr404Error(userId);
        Event event = getEventOr404Error(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Canceling another user's event");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Event id={" + eventId + "} is not pending moderation");
        }
        event.setState(EventState.CANCELED);
        log.debug("Event id={} has been canceled", event.getId());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto publishEventById(Long eventId) {
        int minHoursBeforePublication = 1;
        Event event = getEventOr404Error(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Event id={" + eventId + "} is not pending moderation");
        }
        LocalDateTime minStart = LocalDateTime.now().plusHours(minHoursBeforePublication);
        if (event.getEventDate().isBefore(minStart)) {
            throw new BadRequestException("Event datetime does not meet the conditions for publication");
        }
        event.setState(EventState.PUBLISHED);
        log.debug("Event id={} has been published", event.getId());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto rejectEventById(Long eventId) {
        Event event = getEventOr404Error(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event id={" + eventId + "} already published");
        }
        event.setState(EventState.REJECTED);
        log.debug("Event id={} has been rejected", event.getId());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventFullDto> findEventsByParams(
            Optional<List<Long>> usersOptional,
            Optional<List<String>> statesOptional,
            Optional<List<Long>> categoriesOptional,
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            int from,
            int size
    ) {
        Pageable pageable = SizeRequest.from(from, size);
        BooleanBuilder builder = new BooleanBuilder();
        usersOptional.ifPresent(users -> builder.and(QEvent.event.initiator.id.in(users)));
        statesOptional.ifPresent(states -> {
            builder.and(QEvent.event.state.in(states.stream()
                    .map(EventState::from)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())));
        });
        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));
        addStartEndToBooleanBuilder(rangeStartOptional, rangeEndOptional, builder);
        return EventMapper.toEventFullDto(eventRepository.findAll(builder, pageable));
    }

    public List<EventShortDto> findEventsByParams(
            Optional<String> textOptional,
            Optional<List<Long>> categoriesOptional,
            Optional<Boolean> paidOptional,
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            Optional<Boolean> onlyAvailableOptional,
            Optional<String> sortOptional,
            int from,
            int size
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        textOptional.ifPresent(text -> {
            builder.and(QEvent.event.annotation.likeIgnoreCase(text)
                    .or(QEvent.event.description.likeIgnoreCase(text)));
        });
        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));
        paidOptional.ifPresent(paid -> builder.and(QEvent.event.paid.eq(paid)));
        addStartEndToBooleanBuilder(rangeStartOptional, rangeEndOptional, builder);
        onlyAvailableOptional.ifPresent((available) -> {
            builder.and(QEvent.event.participantLimit.gt(QEvent.event.participants.size()));
        });
        final Pageable[] pageable = {SizeRequest.from(from, size)};
        sortOptional.flatMap(s -> Optional.ofNullable(EventSort.from(s))).ifPresent(sort -> {
            pageable[0] = SizeRequest.from(from, size, Sort.by(sort.name()));
        });
        return EventMapper.toEventShortDto(eventRepository.findAll(builder, pageable[0]));
    }

    private void addStartEndToBooleanBuilder(
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            BooleanBuilder builder
    ) {
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern(format);
        rangeStartOptional.ifPresent(start -> {
            builder.and(QEvent.event.eventDate.after(LocalDateTime.parse(start, formatter)));
        });
        rangeEndOptional.ifPresent(end -> {
            builder.and(QEvent.event.eventDate.before(LocalDateTime.parse(end, formatter)));
        });
    }
}
