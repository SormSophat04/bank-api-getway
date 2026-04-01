package com.lolc.api.rest.mapper;

import com.lolc.api.rest.dto.response.CustomerResponse;
import com.lolc.api.rest.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);
}
