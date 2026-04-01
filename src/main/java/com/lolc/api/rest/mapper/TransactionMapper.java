package com.lolc.api.rest.mapper;

import com.lolc.api.rest.dto.TransactionDTO;
import com.lolc.api.rest.dto.request.TransferRequest;
import com.lolc.api.rest.entity.Transaction;
import com.lolc.api.rest.dto.response.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class, AccountMapper.class})
public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    Transaction toTransaction(TransactionDTO transactionDTO);

    @Mapping(target = "transactionId", ignore = true)
    TransactionDTO toTransactionDTO(Transaction transaction);

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "type", constant = "TRANSFER")
    @Mapping(target = "fromAccountId", ignore = true)
    @Mapping(target = "toAccountId", ignore = true)
    Transaction toEntity(TransferRequest request);

    @Mapping(source = "createAt", target = "createAt")
    TransactionResponse toResponse(Transaction transaction);
}
