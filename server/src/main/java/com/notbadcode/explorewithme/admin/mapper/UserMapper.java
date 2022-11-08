package com.notbadcode.explorewithme.admin.mapper;

import com.notbadcode.explorewithme.admin.dto.UserDto;
import com.notbadcode.explorewithme.admin.dto.UserShortDto;
import com.notbadcode.explorewithme.admin.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public static User toUser(UserShortDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
