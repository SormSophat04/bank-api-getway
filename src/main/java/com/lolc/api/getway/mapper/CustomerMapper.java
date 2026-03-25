package com.lolc.api.getway.mapper;

import com.lolc.api.getway.dto.response.CustomerResponse;
import com.lolc.api.getway.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);
}
