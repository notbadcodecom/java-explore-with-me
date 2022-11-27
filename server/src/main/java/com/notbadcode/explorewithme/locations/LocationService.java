package com.notbadcode.explorewithme.locations;

import com.notbadcode.explorewithme.locations.dto.LocationFullDto;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    Location getLocationById(Long id);

    LocationFullDto createLocation(LocationFullDto locationDto);

    LocationFullDto updateLocation(LocationFullDto locationDto);

    List<LocationFullDto> findLocationsByParams(
            Optional<String> locationName, Optional<String> locationType, int from, int size
    );

    LocationFullDto findLocationsById(Long locationId);
}
