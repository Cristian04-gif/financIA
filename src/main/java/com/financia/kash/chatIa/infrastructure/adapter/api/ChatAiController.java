package com.financia.kash.chatIa.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.chatIa.application.port.input.CreateFinancialConversationUseCase;
import com.financia.kash.chatIa.application.port.input.GetFinancialConversationMessagesUseCase;
import com.financia.kash.chatIa.application.port.input.GetFinancialConversationsUseCase;
import com.financia.kash.chatIa.domain.model.FinancialChatMessage;
import com.financia.kash.chatIa.domain.model.FinancialConversation;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/assistent/chats")
@RequiredArgsConstructor
public class ChatAiController {

    private final GetFinancialConversationsUseCase conversationsUseCase;
    private final GetFinancialConversationMessagesUseCase conversationMessagesUseCase;
    private final CreateFinancialConversationUseCase createFinancialConversationUseCase;

    @GetMapping
    public Flux<FinancialConversation> getAll(@AuthenticationPrincipal UserEntity user) {
        return conversationsUseCase.execute(user.getId());
    }

    @GetMapping("/{id}/messages")
    public Flux<FinancialChatMessage> getMessageByChat(@AuthenticationPrincipal UserEntity user,
            @PathVariable UUID id) {
        return conversationMessagesUseCase.execute(id, user.getId());
    }

    @PostMapping
    public Mono<ResponseEntity<FinancialConversation>> createConversation(@AuthenticationPrincipal UserEntity user) {
        return createFinancialConversationUseCase.execute(user.getId()).map(ResponseEntity::ok);
    }

}
