package com.lolc.api.rest.mapper;

import com.lolc.api.rest.dto.response.CardResponse;
import com.lolc.api.rest.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AccountMapper.class)
public interface CardMapper {

    @Mapping(source = "account", target = "account")
    CardResponse toResponse(Card card);
}
