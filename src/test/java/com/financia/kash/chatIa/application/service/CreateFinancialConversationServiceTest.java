package com.financia.kash.chatIa.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class CreateFinancialConversationServiceTest {
    @Mock
    private FinancialConversationRepositoryPort conversationRepository;

    private CreateFinancialConversationService service;

    @BeforeEach
    void setUp() {
        service = new CreateFinancialConversationService(
                conversationRepository);
    }

    @Test
    void shouldCreateConversation() {

        UUID userId = UUID.randomUUID();

        FinancialConversation conversation = FinancialConversation.create(userId);

        when(conversationRepository.save(
                any(FinancialConversation.class)))
                .thenReturn(Mono.just(conversation));

        StepVerifier.create(service.execute(userId))
                .assertNext(result -> {
                    assertThat(result.id())
                            .isEqualTo(conversation.id());

                    assertThat(result.userId())
                            .isEqualTo(userId);

                    assertThat(result.title())
                            .isEqualTo("Nueva conversación");

                    assertThat(result.active())
                            .isTrue();
                })
                .verifyComplete();

        verify(conversationRepository)
                .save(any(FinancialConversation.class));
    }

    @Test
    void shouldPropagateRepositoryError() {

        UUID userId = UUID.randomUUID();

        RuntimeException exception = new RuntimeException("Error al guardar conversación");

        when(conversationRepository.save(
                any(FinancialConversation.class)))
                .thenReturn(Mono.error(exception));

        StepVerifier.create(service.execute(userId))
                .expectErrorMatches(error -> error == exception)
                .verify();

        verify(conversationRepository)
                .save(any(FinancialConversation.class));
    }
}
