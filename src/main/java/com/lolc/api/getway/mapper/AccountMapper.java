package com.lolc.api.getway.mapper;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.service.AccountService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses =  {AccountService.class})
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "customer.customerId", source = "customerId")
    Account toAccount(AccountDTO accountDTO);

    @Mapping(target = "customerId", source = "customer.customerId")
    AccountDTO toAccountDTO(Account account);

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "customer.customerId", source = "customerId")
    void updateAccountFromDto(AccountDTO accountDTO, @MappingTarget Account account);
}
