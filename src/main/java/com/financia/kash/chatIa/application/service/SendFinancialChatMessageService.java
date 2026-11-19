package com.financia.kash.chatIa.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeFinancialDiagnosisUseCase;
import com.financia.kash.analisis.application.port.input.AnalyzeFinancialFindingsUseCase;
import com.financia.kash.analisis.application.port.input.GetFinancialSummaryUseCase;
import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.chatIa.application.port.input.SendFinancialChatMessageUseCase;
import com.financia.kash.chatIa.application.port.output.FinancialChatAiPort;
import com.financia.kash.chatIa.application.port.output.FinancialChatMessageRepositoryPort;
import com.financia.kash.chatIa.application.port.output.FinancialConversationRepositoryPort;
import com.financia.kash.chatIa.domain.enums.MessageType;
import com.financia.kash.chatIa.domain.model.FinancialChatContext;
import com.financia.kash.chatIa.domain.model.FinancialChatMessage;
import com.financia.kash.chatIa.domain.model.FinancialChatStreamResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SendFinancialChatMessageService implements SendFinancialChatMessageUseCase {

        private final FinancialChatMessageRepositoryPort chatMessageRepositoryPort;
        private final FinancialConversationRepositoryPort conversationRepositoryPort;
        private final int limitMessage;
        private final int historyMessageLimit;
        private final GetFinancialSummaryUseCase financialSummaryUseCase;
        private final AnalyzeFinancialFindingsUseCase findingsUseCase;
        private final AnalyzeFinancialDiagnosisUseCase diagnosisUseCase;
        private final FinancialChatAiPort financialChatAiPort;

        public SendFinancialChatMessageService(FinancialChatMessageRepositoryPort chatMessageRepositoryPort,
                        FinancialConversationRepositoryPort conversationRepositoryPort,
                        @Value("${app.ai.chat.daily-message-limit}") int limitMessage,
                        @Value("${app.ai.chat.history-message-limit}") int historyMessageLimit,
                        GetFinancialSummaryUseCase financialSummaryUseCase,
                        AnalyzeFinancialFindingsUseCase findingsUseCase,
                        AnalyzeFinancialDiagnosisUseCase diagnosisUseCase,
                        FinancialChatAiPort financialChatAiPort) {
                this.chatMessageRepositoryPort = chatMessageRepositoryPort;
                this.conversationRepositoryPort = conversationRepositoryPort;
                this.limitMessage = limitMessage;
                this.historyMessageLimit = historyMessageLimit;
                this.financialSummaryUseCase = financialSummaryUseCase;
                this.findingsUseCase = findingsUseCase;
                this.diagnosisUseCase = diagnosisUseCase;
                this.financialChatAiPort = financialChatAiPort;
        }

        @Override
        public Flux<FinancialChatStreamResponse> execute(
                        UUID userId,
                        UUID conversationId,
                        String message) {

                if (userId == null || conversationId == null) {
                        return Flux.error(
                                        new IllegalArgumentException("El usuario y la conversación son obligatorios"));
                }

                if (message == null || message.isBlank()) {
                        return Flux.error(new IllegalArgumentException("El mensaje no puede estar vacío"));
                }

                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

                return conversationRepositoryPort.findByIdAndUserId(conversationId, userId)
                                .then(chatMessageRepositoryPort.countUserMessagesByUserIdSince(userId, startOfDay))
                                .flatMapMany(count -> {

                                        if (count >= limitMessage) {
                                                return Flux.error(new IllegalArgumentException(
                                                                "Límite de mensajes diarios alcanzado"));
                                        }

                                        FinancialChatMessage userMessage = FinancialChatMessage.create(conversationId,
                                                        MessageType.USER,
                                                        message.trim());

                                        return chatMessageRepositoryPort.save(userMessage)
                                                        .then(buildContext(userId, conversationId))
                                                        .flatMapMany(context -> {

                                                                return Flux.defer(() -> {

                                                                        StringBuilder fullResponse = new StringBuilder();

                                                                        Flux<FinancialChatStreamResponse> chunks = financialChatAiPort
                                                                                        .generateResponse(context)
                                                                                        .doOnNext(fullResponse::append)
                                                                                        .map(chunk -> new FinancialChatStreamResponse(
                                                                                                        "CHUNK",
                                                                                                        conversationId,
                                                                                                        null,
                                                                                                        chunk, null));

                                                                        Mono<FinancialChatStreamResponse> complete = Mono
                                                                                        .defer(() -> {

                                                                                                FinancialChatMessage assistantMessage = FinancialChatMessage
                                                                                                                .create(conversationId,
                                                                                                                                MessageType.ASSISTANT,
                                                                                                                                fullResponse.toString());

                                                                                                return chatMessageRepositoryPort
                                                                                                                .save(assistantMessage)
                                                                                                                .map(savedMessage -> new FinancialChatStreamResponse(
                                                                                                                                "COMPLETE",
                                                                                                                                savedMessage.conversationId(),
                                                                                                                                savedMessage.id(),
                                                                                                                                savedMessage.content(),
                                                                                                                                savedMessage.creationDate()));
                                                                                        });

                                                                        return Flux.concat(
                                                                                        Mono.just(
                                                                                                        new FinancialChatStreamResponse(
                                                                                                                        "START",
                                                                                                                        conversationId,
                                                                                                                        null,
                                                                                                                        null,
                                                                                                                        null)),
                                                                                        chunks,
                                                                                        complete);
                                                                });
                                                        });
                                });
        }

        private Mono<FinancialChatContext> buildContext(UUID userId, UUID conversationId) {

                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.withDayOfMonth(1);

                Mono<List<FinancialChatMessage>> historyMono = chatMessageRepositoryPort
                                .findRecentByConversationId(conversationId, historyMessageLimit).collectList();

                Mono<FinancialSummary> summaryMono = financialSummaryUseCase.execute(userId, startDate, endDate);

                return Mono.zip(historyMono, summaryMono)
                                .flatMap(tuple -> {
                                        List<FinancialChatMessage> history = tuple.getT1();
                                        FinancialSummary summary = tuple.getT2();

                                        return findingsUseCase.analyze(summary)
                                                        .flatMap(findings -> diagnosisUseCase.analyze(summary, findings)
                                                                        .map(diagnosis -> new FinancialChatContext(
                                                                                        history, summary, findings,
                                                                                        diagnosis)));
                                });
        }
}