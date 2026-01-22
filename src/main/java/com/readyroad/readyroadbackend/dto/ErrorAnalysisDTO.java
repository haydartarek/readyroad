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
    // عدد الأخطاء حسب النوع
    private Map<String, Integer> errorTypeCounts;
    
    // الفئات الضعيفة (أسوأ 3)
    private List<WeakCategoryDTO> weakCategories;
}
