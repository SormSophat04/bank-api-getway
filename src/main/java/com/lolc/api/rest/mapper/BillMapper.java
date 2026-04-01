package com.lolc.api.rest.mapper;

import com.lolc.api.rest.dto.request.BillRequest;
import com.lolc.api.rest.dto.response.BillResponse;
import com.lolc.api.rest.entity.Bill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillMapper {

    Bill toEntity(BillRequest request);

    BillRequest toRequest(Bill entity);

    BillResponse toResponse(Bill entity);
}
