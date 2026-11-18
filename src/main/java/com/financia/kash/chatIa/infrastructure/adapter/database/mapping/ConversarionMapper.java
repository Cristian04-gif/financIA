package com.financia.kash.chatIa.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.chatIa.domain.model.FinancialConversation;
import com.financia.kash.chatIa.infrastructure.adapter.database.entity.FinancialConversationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ConversarionMapper {

    FinancialConversation mapToDomain(FinancialConversationEntity entity);

    FinancialConversationEntity mapToEntity(FinancialConversation conversation);
}
