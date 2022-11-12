package com.notbadcode.explorewithme.category;

import com.notbadcode.explorewithme.util.Create;
import com.notbadcode.explorewithme.util.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin: Категории", description = "API для работы с категориями")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @Operation(summary = "Добавление новой категории", description = "Имя категории уникально")
    @ApiResponse(responseCode = "201", description = "Категория добавлена",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Validated(Create.class) @RequestBody CategoryDto categoryDto) {
        log.info("POST /admin/categories : {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @Operation(summary = "Изменение категории", description = "Имя категории уникально")
    @ApiResponse(responseCode = "200", description = "Данные категории изменены")
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@Validated(Update.class) @RequestBody CategoryDto categoryDto) {
        log.info("DELETE /admin/categories : {}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }

    @Operation(summary = "Удаление категории", description = "C категорией не должно быть связано ни одного события")
    @ApiResponse(responseCode = "204", description = "Категория удалена")
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long categoryId) {
        log.info("DELETE /admin/categories/{}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
