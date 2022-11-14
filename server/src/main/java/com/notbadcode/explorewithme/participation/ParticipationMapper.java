package com.notbadcode.explorewithme.participation;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ParticipationMapper {
    public static List<ParticipationDto> toParticipationDto(List<Participation> participations) {
        return participations.stream()
                .map(ParticipationMapper::toParticipationDto)
                .collect(Collectors.toList());
    }

    public static ParticipationDto toParticipationDto(Participation participation) {
        return ParticipationDto.builder()
                .id(participation.getId())
                .event(participation.getEvent().getId())
                .status(participation.getStatus())
                .requester(participation.getParticipant().getId())
                .created(participation.getCreated())
                .build();
    }
}
