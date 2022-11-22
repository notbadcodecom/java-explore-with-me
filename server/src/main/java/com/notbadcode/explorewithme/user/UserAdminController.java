package com.notbadcode.explorewithme.user;

import com.notbadcode.explorewithme.user.dto.UserDto;
import com.notbadcode.explorewithme.user.dto.UserShortDto;
import com.notbadcode.explorewithme.util.ControllerLog;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Пользователи", description = "API для работы с пользователями")
public class UserAdminController {
    private final UserService userService;

    @Operation(summary = "Добавление нового пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto createUser(@Valid @RequestBody UserShortDto userDto, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return userService.createUser(userDto);
    }

    @Operation(summary = "Удаление пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь удален")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        userService.deleteUser(userId);
    }

    @Operation(summary = "Получение информации о пользователях",
            description = "Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), " +
                    "либо о конкретных (учитываются указанные идентификаторы)")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findUsers(
            @RequestParam(name = "ids", required = false) Optional<List<Long>> ids,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
         log.info("{}", ControllerLog.createUrlInfo(request));
        return userService.findUsers(ids, from, size);
    }
}
