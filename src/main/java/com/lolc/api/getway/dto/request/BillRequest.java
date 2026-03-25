package com.lolc.api.getway.dto.request;

import com.lolc.api.getway.enums.BillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BillRequest(
        @NotNull BillType billType,
        @NotBlank String billCode
) {
}