package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorAnalysisDTO {
    // Number of errors by type
    private Map<String, Integer> errorTypeCounts;
    
    // Weak categories (worst 3)
    private List<WeakCategoryDTO> weakCategories;
}
