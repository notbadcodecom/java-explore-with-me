package com.notbadcode.explorewithme.category;

import com.notbadcode.explorewithme.util.Create;
import com.notbadcode.explorewithme.util.Update;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@ToString
public class CategoryDto {
    @NotNull(message = "Category id is required", groups = {Update.class})
    private Long id;

    @NotBlank(message = "Category name is required", groups = {Create.class, Update.class})
    private String name;
}
