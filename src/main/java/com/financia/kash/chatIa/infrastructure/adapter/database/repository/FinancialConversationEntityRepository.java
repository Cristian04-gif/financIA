package com.financia.kash.chatIa.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.chatIa.infrastructure.adapter.database.entity.FinancialConversationEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialConversationEntityRepository
                extends ReactiveCrudRepository<FinancialConversationEntity, UUID> {

        Mono<FinancialConversationEntity> findByIdAndUserId(UUID id, UUID userId);

        Flux<FinancialConversationEntity> findAllByUserId(UUID userId);
}
