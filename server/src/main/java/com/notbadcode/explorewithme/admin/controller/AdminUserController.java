package com.notbadcode.explorewithme.admin.controller;

import com.notbadcode.explorewithme.admin.dto.UserDto;
import com.notbadcode.explorewithme.admin.dto.UserShortDto;
import com.notbadcode.explorewithme.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Пользователи", description = "API для работы с пользователями")
public class AdminUserController {
    private final AdminUserService userService;

    @Operation(summary = "Добавление нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserShortDto userDto) {
        log.info("POST /admin/users : {}", userDto);
        return userService.createUser(userDto);
    }

    @Operation(summary = "Удаление пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь удален")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE /admin/users/{}", userId);
        userService.deleteUser(userId);
    }

    @Operation(summary = "Получение информации о пользователях",
            description = "Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), " +
                    "либо о конкретных (учитываются указанные идентификаторы)")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAllUsers(
            @RequestParam(name = "ids", required = false) Optional<List<Long>> ids,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        log.info("GET /admin/users");
        return userService.findAllUsers(ids, from, size);
    }
}
