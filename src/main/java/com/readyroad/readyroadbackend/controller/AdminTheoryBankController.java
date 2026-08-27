package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.BankHealthResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.CategoryHealth;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.CategoryResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryCategoryRequest;
import com.readyroad.readyroadbackend.service.AdminTheoryBankHealthService;
import com.readyroad.readyroadbackend.service.AdminTheoryCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quiz")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTheoryBankController {

    private final AdminTheoryBankHealthService bankHealthService;
    private final AdminTheoryCategoryService categoryService;

    @GetMapping("/bank-health")
    public ResponseEntity<BankHealthResponse> bankHealth() {
        return ResponseEntity.ok(bankHealthService.bankHealth());
    }

    @GetMapping("/categories/manage")
    public ResponseEntity<List<CategoryHealth>> categoryManagement() {
        return ResponseEntity.ok(bankHealthService.categoryManagement());
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody AdminTheoryCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable long categoryId,
            @Valid @RequestBody AdminTheoryCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }
}
