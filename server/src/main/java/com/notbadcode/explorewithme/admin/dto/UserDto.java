package com.notbadcode.explorewithme.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    Long id;

    String name;

    String email;
}
