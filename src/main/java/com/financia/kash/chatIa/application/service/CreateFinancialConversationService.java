package com.financia.kash.chatIa.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.chatIa.application.port.input.CreateFinancialConversationUseCase;
import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.model.FinancialConversation;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateFinancialConversationService implements CreateFinancialConversationUseCase {

    private final FinancialConversationRepositoryPort conversationRepository;

    @Override
    public Mono<FinancialConversation> execute(UUID userId) {
        return conversationRepository.save(FinancialConversation.create(userId));
    }

}
