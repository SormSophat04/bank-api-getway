package com.lolc.api.getway.dto.response;

public record CustomerResponse(
        Long customerId,
        String firstName,
        String lastName,
        String phone,
        String email,
        Long nationalId,
        String birthDate,
        String status,
        String createAt,
        String updateAt,
        String createBy,
        String updateBy
) {
}
