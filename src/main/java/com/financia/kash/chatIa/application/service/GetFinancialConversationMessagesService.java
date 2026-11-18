package com.financia.kash.chatIa.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.chatIa.application.port.input.GetFinancialConversationMessagesUseCase;
import com.financia.kash.chatIa.application.port.output.FinancialChatMessageRepositoryPort;
import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.model.FinancialChatMessage;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetFinancialConversationMessagesService implements GetFinancialConversationMessagesUseCase {
    private final FinancialConversationRepositoryPort conversationRepository;
    private final FinancialChatMessageRepositoryPort messageRepository;

    @Override
    public Flux<FinancialChatMessage> execute(UUID conversationId, UUID userId) {
        return conversationRepository.findByIdAndUserId(conversationId, userId)
                .thenMany(Flux.defer(() -> messageRepository.findAllByConversationId(conversationId)));
    }

}
