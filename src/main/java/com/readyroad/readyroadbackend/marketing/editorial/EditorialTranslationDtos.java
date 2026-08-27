package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class EditorialTranslationDtos {

    private EditorialTranslationDtos() {}

    public record CreateRequest(
            @NotBlank @Size(max = 255) String idempotencyKey) {}
}