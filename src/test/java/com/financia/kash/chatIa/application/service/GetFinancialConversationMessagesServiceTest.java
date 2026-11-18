package com.financia.kash.chatIa.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financia.kash.chatIa.application.port.output.FinancialChatMessageRepositoryPort;
import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.enums.MessageType;
import com.financia.kash.chatIa.domain.model.FinancialChatMessage;
import com.financia.kash.chatIa.domain.model.FinancialConversation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class GetFinancialConversationMessagesServiceTest {
    @Mock
    private FinancialConversationRepositoryPort conversationRepository;

    @Mock
    private FinancialChatMessageRepositoryPort messageRepository;

    private GetFinancialConversationMessagesService service;

    @BeforeEach
    void setUp() {
        service = new GetFinancialConversationMessagesService(
                conversationRepository,
                messageRepository);
    }

    @Test
    void shouldGetMessagesWhenConversationBelongsToUser() {

        UUID userId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        FinancialConversation conversation = new FinancialConversation(
                conversationId,
                userId,
                "Nueva conversación",
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        FinancialChatMessage message1 = FinancialChatMessage.create(
                conversationId,
                MessageType.USER,
                "¿Cómo están mis gastos?");

        FinancialChatMessage message2 = FinancialChatMessage.create(
                conversationId,
                MessageType.ASSISTANT,
                "Tus gastos han aumentado.");

        when(conversationRepository.findByIdAndUserId(
                conversationId,
                userId))
                .thenReturn(Mono.just(conversation));

        when(messageRepository.findAllByConversationId(
                conversationId))
                .thenReturn(Flux.just(message1, message2));

        StepVerifier.create(
                service.execute(conversationId, userId))
                .expectNext(message1)
                .expectNext(message2)
                .verifyComplete();

        verify(conversationRepository)
                .findByIdAndUserId(conversationId, userId);

        verify(messageRepository)
                .findAllByConversationId(conversationId);
    }

    @Test
    void shouldNotGetMessagesWhenConversationDoesNotBelongToUser() {

        UUID userId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        RuntimeException exception = new RuntimeException("Conversación no encontrada");

        when(conversationRepository.findByIdAndUserId(
                conversationId,
                userId))
                .thenReturn(Mono.error(exception));

        StepVerifier.create(
                service.execute(conversationId, userId))
                .expectErrorMatches(error -> error == exception)
                .verify();

        verify(conversationRepository)
                .findByIdAndUserId(conversationId, userId);

        verify(messageRepository, never())
                .findAllByConversationId(any());
    }

    @Test
    void shouldReturnEmptyWhenConversationHasNoMessages() {

        UUID userId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        FinancialConversation conversation = FinancialConversation.create(userId);

        when(conversationRepository.findByIdAndUserId(
                conversationId,
                userId))
                .thenReturn(Mono.just(conversation));

        when(messageRepository.findAllByConversationId(
                conversationId))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                service.execute(conversationId, userId))
                .verifyComplete();

        verify(messageRepository)
                .findAllByConversationId(conversationId);
    }
}
