package com.notbadcode.explorewithme.locations;

import com.notbadcode.explorewithme.locations.dto.LocationFullDto;
import com.notbadcode.explorewithme.util.ControllerLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: Локации", description = "API для работы с локациями")
public class LocationPublicController {
    private final LocationService locationService;

    @Operation(summary = "Получение локации")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = LocationFullDto.class)))})
    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto findLocations(@PathVariable Long locationId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return locationService.findLocationsById(locationId);
    }

    @Operation(summary = "Получение списка локаций")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = LocationFullDto.class)))})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LocationFullDto> findLocationsByParams(
            @RequestParam(name = "name") Optional<String> locationName,
            @Parameter(allowEmptyValue = true, schema = @Schema(implementation = LocationType.class))
            @RequestParam(name = "type") Optional<String> locationType,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return locationService.findLocationsByParams(locationName, locationType, from, size);
    }
}
