package com.lolc.api.rest.mapper;

import com.lolc.api.rest.dto.request.LoanRequest;
import com.lolc.api.rest.dto.response.LoanResponse;
import com.lolc.api.rest.entity.Customer;
import com.lolc.api.rest.entity.Loan;
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

    Loan updateLoan(Long loanId, LoanRequest loanRequest);

    @Mapping(target = "total_interest", source = "totalInterest")
    LoanResponse toResponse(Loan loan);

    default LoanResponse.CustomerSnapshot toCustomerSnapshot(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new LoanResponse.CustomerSnapshot(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getEmail()
        );
    }
}
