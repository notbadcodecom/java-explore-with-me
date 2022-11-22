package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.dto.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface EventService {
    EventFullDto createEvent(NewEventDto eventDto, Long userId);

    EventFullDto findEventByInitiatorById(Long eventId, Long userId);

    EventFullDto findEventById(HttpServletRequest request, Long eventId);

    Event getEventById(Long eventId);

    List<EventShortDto> findEventsByUserId(Long userId, int from, int size);

    EventFullDto updateEventByUser(UpdateEventDto eventDto, Long userId);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventDto eventDto);

    EventFullDto cancelEventById(Long eventId, Long userId);

    EventFullDto publishEventById(Long eventId);

    EventFullDto rejectEventById(Long eventId);

    List<EventFullDto> findEventsByParams(
            Optional<List<Long>> usersOptional,
            Optional<List<String>> statesOptional,
            Optional<List<Long>> categoriesOptional,
            Optional<String> rangeStartOptional,
            Optional<String> rangeEndOptional,
            int from,
            int size
    );

    List<EventShortDto> findEventsByParams(
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
    );

    List<Event> findEventsByIds(List<Long> ids);

    List<Event> setViewsByStatsServer(List<Event> events);

    void sendStatsByClient(HttpServletRequest request);

    List<Event> setViewsByStatsServer(Page<Event> events);

    Event setViewsByStatsServer(Event event);
}
