package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.event.dto.EventFullDto;
import com.notbadcode.explorewithme.event.dto.EventShortDto;
import com.notbadcode.explorewithme.location.dto.LocationShortDto;
import com.notbadcode.explorewithme.event.dto.NewEventDto;
import com.notbadcode.explorewithme.category.CategoryDto;
import com.notbadcode.explorewithme.user.UserMapper;
import lombok.experimental.UtilityClass;

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
                .lat(eventDto.getLocation().getLat())
                .lon(eventDto.getLocation().getLon())
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
                .location(LocationShortDto.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .views(event.getViews())
                .build();
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
                .views(event.getViews())
                .build();
    }

    public static List<EventShortDto> toEventShortDto(List<Event> event) {
        return event.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public static List<EventFullDto> toEventFullDto(List<Event> event) {
        return event.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }
}
