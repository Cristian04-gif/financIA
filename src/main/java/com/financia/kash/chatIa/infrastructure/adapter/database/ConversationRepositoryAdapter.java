package com.financia.kash.chatIa.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.exception.ConversarionNotFoundException;
import com.financia.kash.chatIa.domain.model.FinancialConversation;
import com.financia.kash.chatIa.infrastructure.adapter.database.mapping.ConversarionMapper;
import com.financia.kash.chatIa.infrastructure.adapter.database.repository.FinancialConversationEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoryAdapter implements FinancialConversationRepositoryPort {
    private final FinancialConversationEntityRepository conversationEntityRepository;
    private final ConversarionMapper mapper;

    @Override
    public Mono<FinancialConversation> save(FinancialConversation conversation) {
        return conversationEntityRepository.save(mapper.mapToEntity(conversation)).map(mapper::mapToDomain);
    }

    @Override
    public Mono<FinancialConversation> findByIdAndUserId(UUID conversationId, UUID userId) {
        return conversationEntityRepository.findByIdAndUserId(conversationId, userId)
                .switchIfEmpty(Mono.error(new ConversarionNotFoundException(conversationId, userId)))
                .map(mapper::mapToDomain);
    }

    @Override
    public Flux<FinancialConversation> findAllByUserId(UUID userId) {
        return conversationEntityRepository.findAllByUserId(userId).map(mapper::mapToDomain);
    }

    @Override
    public Mono<Void> update(FinancialConversation conversation) {
        return conversationEntityRepository.save(mapper.mapToEntity(conversation)).then();
    }

}
