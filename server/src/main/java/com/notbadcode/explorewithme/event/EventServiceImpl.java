package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.category.CategoryService;
import com.notbadcode.explorewithme.stats.StatsClient;
import com.notbadcode.explorewithme.error.BadRequestException;
import com.notbadcode.explorewithme.error.ForbiddenException;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.event.dto.*;
import com.notbadcode.explorewithme.user.User;
import com.notbadcode.explorewithme.user.UserService;
import com.notbadcode.explorewithme.util.SizeRequest;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsClient client;
    private final DateTimeFormatter formatter;
    private final int minHoursBeforePublication;

    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository,
            UserService userService,
            CategoryService categoryService,
            StatsClient client,
            @Value("${spring.jackson.date-format}") String format,
            @Value("${ewm-config.event.service.hoursBeforePublication}") int hours
    ) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.client = client;
        this.formatter = DateTimeFormatter.ofPattern(format);
        this.minHoursBeforePublication = hours;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(NewEventDto eventDto, Long userId) {
        Event event = EventMapper.toEvent(eventDto);
        event.setInitiator(userService.getUserOr404Error(userId));
        event.setCategory(categoryService.getCategoryOr404Error(eventDto.getCategory()));
        event = eventRepository.save(event);
        log.debug("Event id={} has been created", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        boolean exist = eventRepository.existsByCategory_Id(categoryId);
        log.debug("Using category id={}: {}", categoryId, exist);
        return exist;
    }

    @Override
    public EventFullDto findEventById(Long eventId, Long userId) {
        User user = userService.getUserOr404Error(userId);
        Event event = getEventOr404Error(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Requesting another user's event");
        }
        log.debug("Find event id={}", eventId);
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    public EventFullDto findEventById(HttpServletRequest request, Long eventId) {
        Event event = getEventOr404Error(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not published");
        }
        log.debug("Find event id={}", eventId);
        client.sendHit(request.getRequestURI(), request.getRemoteAddr());
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    public Event getEventOr404Error(Long eventId) {
        log.debug("Load event id={}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public List<EventShortDto> findEventsByUserId(Long userId, int from, int size) {
        userService.getUserOr404Error(userId);
        Pageable pageable = SizeRequest.of(from, size);
        List<Event> events = eventRepository.findByInitiator_IdOrderByEventDateDesc(userId, pageable);
        log.debug("Find {} events", events.size());
        return EventMapper.toEventShortDto(setViewsByStatsServer(events));
    }

    @Override
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
        return updateEvent(event, eventDto.getEventDate(), eventDto.getPaid(),
                eventDto.getDescription(), eventDto.getParticipantLimit(), eventDto.getAnnotation(),
                eventDto.getTitle(), eventDto.getCategory());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventDto eventDto) {
        Event event = getEventOr404Error(eventId);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setModeration);
        Optional.ofNullable(eventDto.getLocation())
                .ifPresent(locationDto -> {
                    event.setLat(eventDto.getLocation().getLat());
                    event.setLon(eventDto.getLocation().getLon());
                });
        return updateEvent(event, eventDto.getEventDate(), eventDto.getPaid(),
                eventDto.getDescription(), eventDto.getParticipantLimit(), eventDto.getAnnotation(),
                eventDto.getTitle(), eventDto.getCategory());
    }

    private EventFullDto updateEvent(
            Event event,
            LocalDateTime eventDate,
            Boolean paid,
            String description,
            Integer participantLimit,
            String annotation,
            String title,
            Long category
    ) {
        Optional.ofNullable(eventDate).ifPresent(event::setEventDate);
        Optional.ofNullable(paid).ifPresent(event::setPaid);
        Optional.ofNullable(description).ifPresent(event::setDescription);
        Optional.ofNullable(participantLimit).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(annotation).ifPresent(event::setAnnotation);
        Optional.ofNullable(title).ifPresent(event::setTitle);
        Optional.ofNullable(category)
                .ifPresent(e -> event.setCategory(categoryService.getCategoryOr404Error(e)));
        eventRepository.save(event);
        log.debug("Event id={} has been updated", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
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
        return EventMapper.toEventFullDto(setViewsByStatsServer(eventRepository.save(event)));
    }

    @Override
    @Transactional
    public EventFullDto publishEventById(Long eventId) {
        Event event = getEventOr404Error(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Event id={" + eventId + "} is not pending moderation");
        }
        LocalDateTime minStart = LocalDateTime.now().plusHours(minHoursBeforePublication);
        if (event.getEventDate().isBefore(minStart)) {
            throw new BadRequestException("Event datetime does not meet the conditions for publication");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        log.debug("Event id={} has been published", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(eventRepository.save(event)));
    }

    @Override
    @Transactional
    public EventFullDto rejectEventById(Long eventId) {
        Event event = getEventOr404Error(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event id={" + eventId + "} already published");
        }
        event.setState(EventState.CANCELED);
        log.debug("Event id={} has been rejected", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(eventRepository.save(event)));
    }

    @Override
    public List<EventFullDto> findEventsByParams(
            Optional<List<Long>> usersOptional,
            Optional<List<String>> statesOptional,
            Optional<List<Long>> categoriesOptional,
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            int from,
            int size
    ) {
        Pageable pageable = SizeRequest.of(from, size);
        BooleanBuilder builder = new BooleanBuilder();
        usersOptional.ifPresent(users -> builder.and(QEvent.event.initiator.id.in(users)));
        statesOptional.ifPresent(states -> builder.and(QEvent.event.state.in(states.stream()
                .map(EventState::from)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))));
        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));
        addStartEndToBooleanBuilder(rangeStartOptional, rangeEndOptional, builder);
        log.debug("Events found by params");
        return EventMapper.toEventFullDto(setViewsByStatsServer(eventRepository.findAll(builder, pageable)));
    }

    @Override
    public List<EventShortDto> findEventsByParams(
            HttpServletRequest request,
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
        textOptional.ifPresent(text -> builder.and(QEvent.event.annotation.likeIgnoreCase(text)
                .or(QEvent.event.description.likeIgnoreCase(text))));
        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));
        paidOptional.ifPresent(paid -> builder.and(QEvent.event.paid.eq(paid)));
        addStartEndToBooleanBuilder(rangeStartOptional, rangeEndOptional, builder);
        onlyAvailableOptional.ifPresent((available) -> {
            builder.and(QEvent.event.participantLimit.gt(QEvent.event.participants.size()));
        });
        client.sendHit(request.getRequestURI(), request.getRemoteAddr());
        List<Event> events = setViewsByStatsServer(eventRepository.findAll(builder, SizeRequest.of(from, size)));
        sortOptional.flatMap(s -> Optional.ofNullable(EventSort.from(s))).ifPresent(sort -> {
            switch (sort) {
                case EVENT_DATE:
                    events.sort(Comparator.comparing(Event::getEventDate).reversed());
                    break;
                case VIEWS:
                    events.sort(Comparator.comparing(Event::getViews).reversed());
                    break;
                default:
                    events.sort(Comparator.comparing(Event::getId));
            }
        });
        log.debug("Events found by params");
        return EventMapper.toEventShortDto(events);
    }

    @Override
    public List<Event> findEventsByIds(List<Long> ids) {
        log.debug("Events found by ids");
        return eventRepository.findByIdIn(ids);
    }

    @Override
    public void addStartEndToBooleanBuilder(
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            BooleanBuilder builder
    ) {
        rangeStartOptional.ifPresent(start -> {
            builder.and(QEvent.event.eventDate.after(LocalDateTime.parse(start, formatter)));
        });
        rangeEndOptional.ifPresent(end -> {
            builder.and(QEvent.event.eventDate.before(LocalDateTime.parse(end, formatter)));
        });
    }

    @Override
    public List<Event> setViewsByStatsServer(List<Event> events) {
        Map<Long, Long> views = client.getStatsOfEvents(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        return events.stream()
                .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> setViewsByStatsServer(Page<Event> events) {
        Map<Long, Long> views = client.getStatsOfEvents(events.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        return events.stream()
                .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public Event setViewsByStatsServer(Event event) {
        Map<Long, Long> views = client.getStatsOfEvents(List.of(event.getId()));
        event.setViews(views.getOrDefault(event.getId(), 0L));
        return event;
    }
}
