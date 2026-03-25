package com.lolc.api.getway.mapper;

import com.lolc.api.getway.dto.response.CardResponse;
import com.lolc.api.getway.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AccountMapper.class)
public interface CardMapper {

    @Mapping(source = "account", target = "account")
    CardResponse toResponse(Card card);
}
