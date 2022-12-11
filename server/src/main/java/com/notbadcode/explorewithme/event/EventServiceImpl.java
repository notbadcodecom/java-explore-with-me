package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.category.CategoryService;
import com.notbadcode.explorewithme.location.Location;
import com.notbadcode.explorewithme.location.LocationService;
import com.notbadcode.explorewithme.stats.StatsClient;
import com.notbadcode.explorewithme.error.BadRequestException;
import com.notbadcode.explorewithme.error.ForbiddenException;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.event.dto.*;
import com.notbadcode.explorewithme.user.User;
import com.notbadcode.explorewithme.user.UserService;
import com.notbadcode.explorewithme.util.CommonDateTime;
import com.notbadcode.explorewithme.util.SizeRequest;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final StatsClient client;
    private final int minHoursBeforePublication;

    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository,
            UserService userService,
            CategoryService categoryService,
            LocationService locationService,
            StatsClient client,
            @Value("${ewm-config.event.service.hoursBeforePublication}") int hours
    ) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.client = client;
        this.minHoursBeforePublication = hours;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(NewEventDto eventDto, Long userId) {
        Event event = EventMapper.toEvent(eventDto);
        event.setInitiator(userService.getUserById(userId));
        event.setCategory(categoryService.getCategoryById(eventDto.getCategory()));
        event = eventRepository.save(event);
        log.debug("Event id={} has been created", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    public EventFullDto findEventByInitiatorById(Long eventId, Long initiatorId) {
        User initiator = userService.getUserById(initiatorId);
        Event event = getEventById(eventId);
        if (!initiator.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Requesting another user's event");
        }
        log.debug("Find event id={}", eventId);
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    public EventFullDto findEventById(HttpServletRequest request, Long eventId) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not published");
        }
        log.debug("Find event id={}", eventId);
        sendStatsByClient(request);
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    public Event getEventById(Long eventId) {
        log.debug("Load event id={}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public List<EventShortDto> findEventsByUserId(Long userId, int from, int size) {
        userService.getUserById(userId);
        Pageable pageable = SizeRequest.of(from, size);
        List<Event> events = eventRepository.findByInitiator_IdOrderByEventDateDesc(userId, pageable);
        log.debug("Find {} events", events.size());
        return EventMapper.toEventShortDto(setViewsByStatsServer(events));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(UpdateEventDto eventDto, Long userId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventDto.getEventId());
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Updating another user's event");
        }
        checkPendingEventState(event);
        return updateEvent(event, eventDto.getEventDate(), eventDto.getPaid(),
                eventDto.getDescription(), eventDto.getParticipantLimit(), eventDto.getAnnotation(),
                eventDto.getTitle(), eventDto.getCategory());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventDto eventDto) {
        Event event = getEventById(eventId);
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
                .ifPresent(e -> event.setCategory(categoryService.getCategoryById(e)));
        eventRepository.save(event);
        log.debug("Event id={} has been updated", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(event));
    }

    @Override
    @Transactional
    public EventFullDto cancelEventById(Long eventId, Long userId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Canceling another user's event");
        }
        checkPendingEventState(event);
        event.setState(EventState.CANCELED);
        log.debug("Event id={} has been canceled", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(eventRepository.save(event)));
    }

    @Override
    @Transactional
    public EventFullDto publishEventById(Long eventId) {
        Event event = getEventById(eventId);
        checkPendingEventState(event);
        LocalDateTime minStart = LocalDateTime.now().plusHours(minHoursBeforePublication);
        if (event.getEventDate().isBefore(minStart)) {
            throw new BadRequestException("Event datetime does not meet the conditions for publication");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        log.debug("Event id={} has been published", event.getId());
        return EventMapper.toEventFullDto(setViewsByStatsServer(eventRepository.save(event)));
    }

    private void checkPendingEventState(Event event) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Event id={" + event.getId() + "} is not pending moderation");
        }
    }

    @Override
    @Transactional
    public EventFullDto rejectEventById(Long eventId) {
        Event event = getEventById(eventId);
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
            int from, int size
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QEvent.event.state.eq(EventState.PUBLISHED));
        textOptional.ifPresent(text -> builder.and(QEvent.event.annotation.likeIgnoreCase("%" + text + "%")
                .or(QEvent.event.description.likeIgnoreCase("%" + text + "%"))));
        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));
        paidOptional.ifPresent(paid -> builder.and(QEvent.event.paid.eq(paid)));
        addStartEndToBooleanBuilder(rangeStartOptional, rangeEndOptional, builder);
        onlyAvailableOptional.ifPresent(available -> {
            builder.and(QEvent.event.participantLimit.gt(QEvent.event.participants.size()));
        });
        sendStatsByClient(request);
        List<Event> events = new ArrayList<>();
        Pageable pageable = SizeRequest.of(from, size);
        if (sortOptional.isPresent()) {
            if (EventSort.from(sortOptional.get()).equals(EventSort.EVENT_DATE)) {
                pageable = SizeRequest.of(from, size, Sort.by(Sort.Direction.DESC,"eventDate"));
                events = setViewsByStatsServer(eventRepository.findAll(builder, pageable));
            } else if (EventSort.from(sortOptional.get()).equals(EventSort.VIEWS)) {
                for (Event event : eventRepository.findAll(builder)) {
                    events.add(event);
                }
                events = setViewsByStatsServer(events);
                events.sort((event1, event2) -> (int) (event2.getViews() - event1.getViews()));
                events =  events.subList(from, from + size);
            }
        } else {
            events = setViewsByStatsServer(eventRepository.findAll(builder, pageable));
        }
        log.debug("Events found by params");
        return EventMapper.toEventShortDto(events);
    }

    @Override
    public List<Event> findEventsByIds(List<Long> ids) {
        log.debug("Events found by ids");
        return eventRepository.findByIdIn(ids);
    }

    private void addStartEndToBooleanBuilder(
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            BooleanBuilder builder
    ) {
        rangeStartOptional.ifPresent(start -> {
            builder.and(QEvent.event.eventDate.after(CommonDateTime.ofString(start)));
        });
        rangeEndOptional.ifPresent(end -> {
            builder.and(QEvent.event.eventDate.before(CommonDateTime.ofString(end)));
        });
        if (rangeStartOptional.isEmpty() && rangeEndOptional.isEmpty()) {
            builder.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }
    }

    @Override
    public void sendStatsByClient(HttpServletRequest request) {
        try {
            client.sendHit(request.getRequestURI(), request.getRemoteAddr());
        } catch (WebClientRequestException exception) {
            log.info("Stats client connection refused: {}", exception.getMessage());
        }
    }

    @Override
    public List<Event> setViewsByStatsServer(Page<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        try {
            views = client.getStatsOfEvents(events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList()));
        } catch (WebClientRequestException exception) {
            log.info("Stats client connection refused: {}", exception.getMessage());
        }
        Map<Long, Long> finalViews = views;
        return events.stream()
                .peek(e -> e.setViews(finalViews.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> setViewsByStatsServer(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        try {
            views = client.getStatsOfEvents(events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList()));
        } catch (WebClientRequestException exception) {
            log.info("Stats client connection refused: {}", exception.getMessage());
        }
        Map<Long, Long> finalViews = views;
        return events.stream()
                .peek(e -> e.setViews(finalViews.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public Event setViewsByStatsServer(Event event) {
        Map<Long, Long> views = new HashMap<>();
        try {
            views = client.getStatsOfEvents(List.of(event.getId()));
        } catch (WebClientRequestException exception) {
            log.info("Stats client connection refused: {}", exception.getMessage());
        }
        event.setViews(views.getOrDefault(event.getId(), 0L));
        return event;
    }

    @Override
    public List<EventShortDto> findEventsInLocation(Long locationId) {
        Location location = locationService.getLocationById(locationId);
        List<Event> events = eventRepository.findIncludedInLocation(
                location.getLat(), location.getLon(), location.getRadius()
        );
        log.info("Found {} events in location id={}", events.size(), locationId);
        return EventMapper.toEventShortDto(events);
    }

    @Override
    public EventFullDto createEventInLocation(NewEventDto eventDto, Long userId, Long locationId) {
        Location location = locationService.getLocationById(locationId);
        Double lat1 = Optional.ofNullable(eventDto.getLocation().getLat())
                .orElseThrow(() -> new BadRequestException("Wrong location"));
        Double lon1 = Optional.ofNullable(eventDto.getLocation().getLon())
                .orElseThrow(() -> new BadRequestException("Wrong location"));
        // переводим градусы широты в радианы
        double radLat1 = Math.PI * lat1 / 180;
        // переводим градусы долготы в радианы
        double radLat2 = Math.PI * location.getLat() / 180;
        // находим разность долгот
        double theta = lon1 - location.getLon();
        // переводим градусы в радианы
        double radTheta = Math.PI * theta / 180;
        // находим длину ортодромии
        double dist = Math.sin(radLat1) * Math.sin(radLat2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radTheta);
        dist = (dist > 1) ? 1 : dist;
        dist = Math.acos(dist);
        // переводим радианы в градусы
        dist = dist * 180 / Math.PI;
        // переводим градусы в километры
        dist = dist * 60 * 1.8524;
        if (lat1.equals(location.getLat()) && lon1.equals(location.getLon()) || dist <= location.getRadius()) {
            createEvent(eventDto, userId);
        }
        throw new BadRequestException("Wrong location");
    }
}
