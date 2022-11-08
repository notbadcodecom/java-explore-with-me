package com.notbadcode.explorewithme.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@ToString
public class UserShortDto {
    @NotBlank
    String name;

    @NotBlank
    @Email
    String email;
}
