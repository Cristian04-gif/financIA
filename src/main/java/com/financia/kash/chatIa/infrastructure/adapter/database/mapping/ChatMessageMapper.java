package com.financia.kash.chatIa.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.chatIa.domain.model.FinancialChatMessage;
import com.financia.kash.chatIa.infrastructure.adapter.database.entity.FinancialChatMessageEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChatMessageMapper {

    FinancialChatMessage mapToDomain(FinancialChatMessageEntity entity);

    FinancialChatMessageEntity mapToEntity(FinancialChatMessage chatMessage);
}
