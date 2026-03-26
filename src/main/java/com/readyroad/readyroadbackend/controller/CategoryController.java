package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.CategoryResponse;
import com.readyroad.readyroadbackend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * REST Controller for managing traffic sign categories
 * 
 * @author ReadyRoad Team
 * @version 1.0
 * @since 2026-02-04
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Categories", 
    description = "Traffic sign category management"
)
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Get all categories",
        description = "Retrieve all active categories with multi-language support"
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.debug("REST request to get all active categories");
        
        List<CategoryResponse> categories = categoryService.getAllPublicTrafficSignCategories();
        
        log.debug("Returning {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @Operation(
        summary = "Get category by code",
        description = "Retrieve a specific category by its unique code (A-Z)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Category found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Category not found"
        )
    })
    @GetMapping("/{code}")
    public ResponseEntity<CategoryResponse> getCategoryByCode(
        @Parameter(description = "Category code (A-Z)", example = "A")
        @PathVariable 
        @NotBlank(message = "Category code cannot be blank")
        @Size(min = 1, max = 1, message = "Category code must be exactly 1 character")
        String code
    ) {
        log.debug("REST request to get category with code: {}", code);
        
        CategoryResponse category = categoryService.getPublicTrafficSignCategoryByCode(code.toUpperCase());
        
        log.debug("Category found successfully: {}", code.toUpperCase());
        return ResponseEntity.ok(category);
    }
}
