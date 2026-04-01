package com.lolc.api.rest.dto.request;

import com.lolc.api.rest.enums.BillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BillRequest(
        @NotNull BillType billType,
        @NotBlank String billCode
) {
}