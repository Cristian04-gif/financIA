package com.financia.kash.chatIa.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.chatIa.application.port.output.FinancialChatMessageRepositoryPort;
import com.financia.kash.chatIa.domain.model.FinancialChatMessage;
import com.financia.kash.chatIa.infrastructure.adapter.database.mapping.ChatMessageMapper;
import com.financia.kash.chatIa.infrastructure.adapter.database.repository.FinancialChatMessageEntityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Log4j2
public class ChatMessageRepositoryAdapter implements FinancialChatMessageRepositoryPort {

    private final FinancialChatMessageEntityRepository chatMessageEntityRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public Mono<FinancialChatMessage> save(FinancialChatMessage message) {
        return chatMessageEntityRepository.save(chatMessageMapper.mapToEntity(message))
                .map(chatMessageMapper::mapToDomain);
    }

    @Override
    public Flux<FinancialChatMessage> findAllByConversationId(UUID conversationId) {
        return chatMessageEntityRepository.findAllByConversationId(conversationId).map(chatMessageMapper::mapToDomain);
    }

    @Override
    public Flux<FinancialChatMessage> findRecentByConversationId(UUID conversationId, int limit) {
        return chatMessageEntityRepository.findRecentByConversationId(conversationId, limit)
                .map(chatMessageMapper::mapToDomain);
    }

}
