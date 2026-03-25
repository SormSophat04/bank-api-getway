package com.lolc.api.getway.mapper;

import com.lolc.api.getway.dto.request.BillRequest;
import com.lolc.api.getway.dto.response.BillResponse;
import com.lolc.api.getway.entity.Bill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillMapper {

    Bill toEntity(BillRequest request);

    BillRequest toRequest(Bill entity);

    BillResponse toResponse(Bill entity);
}
