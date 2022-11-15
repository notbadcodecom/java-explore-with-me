package com.notbadcode.explorewithme.category;

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

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public: Категории", description = "Публичный API для работы с категориями")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @Operation(summary = "Получение категорий")
    @ApiResponse(responseCode = "200", description = "Категории найдены",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))})
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> findAllCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /categories");
        return categoryService.findAllCategories(from, size);
    }

    @Operation(summary = "Получение информации о категории по её идентификатору")
    @ApiResponse(responseCode = "200", description = "Категория найдена",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDto.class))})
    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto findCategoriesById(@PathVariable Long catId) {
        log.info("GET /categories/{}", catId);
        return categoryService.findCategoriesById(catId);
    }
}
