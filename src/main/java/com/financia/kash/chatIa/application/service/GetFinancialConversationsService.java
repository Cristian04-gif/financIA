package com.financia.kash.chatIa.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.chatIa.application.port.input.GetFinancialConversationsUseCase;
import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.model.FinancialConversation;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetFinancialConversationsService implements GetFinancialConversationsUseCase {
    private final FinancialConversationRepositoryPort conversationRepository;

    @Override
    public Flux<FinancialConversation> execute(UUID userId) {
        return conversationRepository.findAllByUserId(userId);
    }

}
