package com.financia.kash.movimiento.comprobante.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeTypeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.movimiento.comprobante.application.port.input.command.OrcProcessCommand;
import com.financia.kash.movimiento.comprobante.application.port.output.*;
import com.financia.kash.movimiento.comprobante.infrastructure.config.AiCascadeProperties;
import reactor.core.publisher.*;

class VoucherMetricsTest {
    private final ChatClient client = mock(ChatClient.class);
    private final ChatClient.ChatClientRequestSpec request = mock(ChatClient.ChatClientRequestSpec.class, RETURNS_SELF);
    private final ChatClient.CallResponseSpec response = mock(ChatClient.CallResponseSpec.class);
    private final OcrMetricsPort metrics = mock(OcrMetricsPort.class);
    private VoucherService service;
    private final OrcProcessCommand command = new OrcProcessCommand(UUID.randomUUID(), new byte[]{1}, MimeTypeUtils.IMAGE_PNG);

    @BeforeEach
    void setup() {
        when(client.prompt()).thenReturn(request);
        when(request.call()).thenReturn(response);
        when(metrics.record(anyBoolean())).thenReturn(Mono.empty());
        var properties = new AiCascadeProperties();
        properties.setModelsCascade(List.of("primary", "fallback"));
        var categories = mock(CategoryForVoucherPort.class);
        var accounts = mock(AccoutsForVoucherPort.class);
        when(categories.findCategoryForVoucher(any(), anyString())).thenReturn(Flux.empty());
        when(accounts.findAllMyAccountsAndType(any(), anyString())).thenReturn(Flux.empty());
        when(accounts.findAllMyAccounts(any())).thenReturn(Flux.empty());
        service = new VoucherService(client, new ObjectMapper(), properties, categories, accounts, metrics);
    }

    @Test
    void modelFallbackCountsOneSuccessfulRequest() {
        when(response.content()).thenThrow(new IllegalStateException("quota"))
                .thenReturn("{\"expenseCategory\":\"Alimentos\",\"paymentMethod\":\"EFECTIVO\"}");
        assertNotNull(service.OcrProcess(command).block());
        verify(metrics).record(true);
        verify(metrics, never()).record(false);
    }

    @Test
    void failedCascadeCountsOneFailureAndKeepsTheOriginalError() {
        when(response.content()).thenThrow(new IllegalStateException("quota"));
        assertThrows(IllegalArgumentException.class, () -> service.OcrProcess(command).block());
        verify(metrics).record(false);
        verify(metrics, never()).record(true);
    }

    @Test
    void unavailableMetricsDoNotBreakSuccessfulOcr() {
        when(response.content()).thenReturn("{\"expenseCategory\":\"Alimentos\",\"paymentMethod\":\"EFECTIVO\"}");
        when(metrics.record(true)).thenReturn(Mono.error(new IllegalStateException("database unavailable")));
        assertNotNull(service.OcrProcess(command).block());
        verify(metrics, never()).record(false);
    }
}
