package com.lolc.api.getway.mapper;

import com.lolc.api.getway.dto.request.LoanRequest;
import com.lolc.api.getway.dto.response.LoanResponse;
import com.lolc.api.getway.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "loanId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    Loan toEntity(LoanRequest loanRequest);

    LoanResponse toResponse(Loan loan);
}
