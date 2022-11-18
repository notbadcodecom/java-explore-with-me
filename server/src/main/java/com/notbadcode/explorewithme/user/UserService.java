package com.notbadcode.explorewithme.user;

import com.notbadcode.explorewithme.user.dto.UserDto;
import com.notbadcode.explorewithme.user.dto.UserShortDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserShortDto userDto);

    void deleteUser(Long userId);

    List<UserDto> findUsers(Optional<List<Long>> ids, int from, int size);

    User getUserOr404Error(Long userId);
}
