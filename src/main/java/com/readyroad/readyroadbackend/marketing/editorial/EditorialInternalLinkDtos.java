package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class EditorialInternalLinkDtos {

    private EditorialInternalLinkDtos() {}

    public record Input(
            @NotBlank @Size(max = 500) String targetPath,
            @NotBlank @Size(max = 500) String anchorText) {}

    public record Link(
            String type,
            String targetPath,
            String anchorText) {}
}
