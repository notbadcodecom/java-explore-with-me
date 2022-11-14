package ru.practicum.explorewithme.event;

import ru.practicum.explorewithme.category.CategoryDto;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.event.dto.LocationDto;
import ru.practicum.explorewithme.event.dto.NewEventDto;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventState;
import ru.practicum.explorewithme.event.model.Location;
import ru.practicum.explorewithme.user.UserMapper;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    public static Event toEvent(NewEventDto eventDto) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .moderation(eventDto.isRequestModeration())
                .title(eventDto.getTitle())
                .state(EventState.PENDING)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        int confirmedRequests = Optional.ofNullable(event.getParticipants())
                .orElse(new ArrayList<>()).size();
        return EventFullDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .requestModeration(event.getModeration())
                .title(event.getTitle())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(confirmedRequests)
                .state(event.getState())
                .publishedOn(event.getPublishedOn())
                .createdOn(event.getCreatedOn())
                .category(CategoryDto.builder()
                        .id(event.getCategory().getId())
                        .name(event.getCategory().getName())
                        .build())
                .location(toLocationDto(event.getLocation()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .views(0) // todo подгружать просмотры с сервера статистики
                .build();
    }

    public static List<EventFullDto> toEventFullDto(Page<Event> event) {
        return event.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event) {
        int confirmedRequests = Optional.ofNullable(event.getParticipants())
                .orElse(new ArrayList<>()).size();
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .paid(event.getPaid())
                .confirmedRequests(confirmedRequests)
                .category(CategoryDto.builder()
                        .id(event.getCategory().getId())
                        .name(event.getCategory().getName())
                        .build())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .views(0) // todo подгружать просмотры с сервера статистики
                .build();
    }

    public static List<EventShortDto> toEventShortDto(List<Event> event) {
        return event.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public static List<EventShortDto> toEventShortDto(Page<Event> event) {
        return event.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
