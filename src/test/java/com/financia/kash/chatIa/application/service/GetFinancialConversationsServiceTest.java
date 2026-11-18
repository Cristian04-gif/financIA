package com.financia.kash.chatIa.application.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.model.FinancialConversation;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class GetFinancialConversationsServiceTest {
    @Mock
    private FinancialConversationRepositoryPort conversationRepository;

    private GetFinancialConversationsService service;

    @BeforeEach
    void setUp() {
        service = new GetFinancialConversationsService(
                conversationRepository);
    }

    @Test
    void shouldGetConversationsByUserId() {

        UUID userId = UUID.randomUUID();

        FinancialConversation conversation1 = FinancialConversation.create(userId);

        FinancialConversation conversation2 = FinancialConversation.create(userId);

        when(conversationRepository.findAllByUserId(userId))
                .thenReturn(Flux.just(
                        conversation1,
                        conversation2));

        StepVerifier.create(service.execute(userId))
                .expectNext(conversation1)
                .expectNext(conversation2)
                .verifyComplete();

        verify(conversationRepository)
                .findAllByUserId(userId);
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoConversations() {

        UUID userId = UUID.randomUUID();

        when(conversationRepository.findAllByUserId(userId))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.execute(userId))
                .verifyComplete();

        verify(conversationRepository)
                .findAllByUserId(userId);
    }
}
