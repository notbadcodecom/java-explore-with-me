package com.notbadcode.explorewithme.user;

import com.notbadcode.explorewithme.user.dto.UserDto;
import com.notbadcode.explorewithme.user.dto.UserShortDto;
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

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
