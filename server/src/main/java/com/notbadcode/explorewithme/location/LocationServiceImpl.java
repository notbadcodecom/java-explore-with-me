package com.notbadcode.explorewithme.location;

import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.location.dto.LocationFullDto;
import com.notbadcode.explorewithme.util.SizeRequest;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("location id=" + id + "was not found"));
    }

    @Override
    @Transactional
    public LocationFullDto createLocation(LocationFullDto locationDto) {
        return Stream.of(locationDto)
                .map(LocationMapper::toLocation)
                .map(locationRepository::save)
                .map(LocationMapper::toLocationFullDto)
                .peek(location -> log.debug("Save new location : {}", location))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("database saving error"));
    }

    @Override
    @Transactional
    public LocationFullDto updateLocation(LocationFullDto locationDto) {
        Location location = getLocationById(locationDto.getId());
        Optional.ofNullable(locationDto.getLat()).ifPresent(location::setLat);
        Optional.ofNullable(locationDto.getLon()).ifPresent(location::setLon);
        Optional.ofNullable(locationDto.getType()).ifPresent(type -> location.setType(LocationType.from(type)));
        Optional.ofNullable(locationDto.getName()).ifPresent(location::setName);
        Optional.ofNullable(locationDto.getRadius()).ifPresent(location::setRadius);
        return Stream.of(location)
                .map(locationRepository::save)
                .map(LocationMapper::toLocationFullDto)
                .peek(loc -> log.debug("Update location : {}", loc))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("database saving error"));
    }

    @Override
    public List<LocationFullDto> findLocationsByParams(
            Optional<String> locationName,
            Optional<String> locationType,
            int from,
            int size
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        locationName.ifPresent(text -> builder.and(QLocation.location.name.likeIgnoreCase("%" + text + "%")));
        locationType.ifPresent(text -> builder.and(QLocation.location.type.eq(LocationType.from(text))));
        Pageable pageable = SizeRequest.of(from, size);
        Page<Location> locations = locationRepository.findAll(builder, pageable);
        log.debug("Found {} locations by params", locations.getSize());
        return LocationMapper.toLocationFullDto(locations);
    }

    @Override
    public LocationFullDto findLocationsById(Long locationId) {
        Location location = getLocationById(locationId);
        log.debug("Found locations id={}", location.getId());
        return LocationMapper.toLocationFullDto(location);
    }
}
