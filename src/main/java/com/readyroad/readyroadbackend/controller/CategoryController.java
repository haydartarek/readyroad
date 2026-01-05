package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.CategoryResponse;
import com.readyroad.readyroadbackend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllActiveCategories());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CategoryResponse> getCategoryByCode(@PathVariable String code) {
        return ResponseEntity.ok(categoryService.getCategoryByCode(code));
    }
}

