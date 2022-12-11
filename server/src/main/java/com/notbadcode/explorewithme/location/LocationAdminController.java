package com.notbadcode.explorewithme.location;

import com.notbadcode.explorewithme.location.dto.LocationFullDto;
import com.notbadcode.explorewithme.util.ControllerLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Локации", description = "API для работы с локациями")
public class LocationAdminController {
    private final LocationService locationService;

    @Operation(summary = "Добавление новой локации", description = "Радиус локации указывается в метрах")
    @ApiResponse(responseCode = "200", description = "Локация добавлена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = LocationFullDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto createLocation(
            @Valid @RequestBody LocationFullDto locationDto,
            HttpServletRequest request
    ) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return locationService.createLocation(locationDto);
    }

    @Operation(summary = "Редактирование локации")
    @ApiResponse(responseCode = "200", description = "Локация отредактирована",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = LocationFullDto.class))})
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto updateLocation(@RequestBody LocationFullDto locationDto, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return locationService.updateLocation(locationDto);
    }
}
