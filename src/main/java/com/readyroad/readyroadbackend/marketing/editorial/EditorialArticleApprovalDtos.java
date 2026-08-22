package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public final class EditorialArticleApprovalDtos {

    private EditorialArticleApprovalDtos() {
    }

    public record Request(
            @NotEmpty Set<EditorialArticleQualityGate> passedQualityGates,
            @NotBlank @Size(max = 1000) String reason) {
    }
}
