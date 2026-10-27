package com.financia.kash.movimiento.comprobante.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.comprobante.application.port.input.OcrUseCase;
import com.financia.kash.movimiento.comprobante.application.port.input.command.OrcProcessCommand;
import com.financia.kash.movimiento.comprobante.application.port.output.AccoutsForVoucherPort;
import com.financia.kash.movimiento.comprobante.application.port.output.CategoryForVoucherPort;
import com.financia.kash.movimiento.comprobante.domain.model.AccountVoucher;
import com.financia.kash.movimiento.comprobante.domain.model.CategoryVoucher;
import com.financia.kash.movimiento.comprobante.domain.model.Voucher;
import com.financia.kash.movimiento.comprobante.infrastructure.config.AiCascadeProperties;
import com.financia.kash.shared.domain.utils.statics.Ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Log4j2
public class VoucherService implements OcrUseCase {

        private final ChatClient chatClient;
        private final ObjectMapper objectMapper;
        private final AiCascadeProperties aiCascadeProperties;
        private final CategoryForVoucherPort categoryForVoucherPort;
        private final AccoutsForVoucherPort accoutsForVoucherPort;

        @Override
        public Mono<Voucher> OcrProcess(OrcProcessCommand command) {
                List<String> models = aiCascadeProperties.getModelsCascade();
                Resource imageResource = new ByteArrayResource(command.imageByte());

                return Flux.fromIterable(models)
                                .concatMap(modelName -> {
                                        log.info("probando OCR con modelo IA: {}", modelName);
                                        return ocrIA(imageResource, modelName, command.mimeType())
                                                        .onErrorResume(e -> {
                                                                log.warn("Fallo con el modelo {} Buscando respaldo... Error: {}",
                                                                                modelName,
                                                                                e.getMessage());
                                                                return Mono.empty();
                                                        });
                                }).next()
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                                "Error: Ningun modelo de IA prudo procesar el comprobante o se superaron las cuotas")))
                                .flatMap(res -> mapModel(command.userId(), res));
        }

        private Mono<String> ocrIA(Resource image, String modelName, MimeType mimeType) {

                return Mono.fromSupplier(() -> chatClient.prompt()
                                .options(GoogleGenAiChatOptions.builder()
                                                .model(modelName)
                                                .temperature(0.2))
                                .user(userSpec -> userSpec.text(Ocr.PROMPT_ANALIZER_VOUCHER)
                                                .media(mimeType, image))
                                .call()
                                .content())
                                .subscribeOn(Schedulers.boundedElastic());
        }

        private Mono<Voucher> mapModel(UUID userId, String res) {
                try {
                        // Limpieza preventiva en caso de que el modelo devuelva bloques ```json ... ```
                        String cleanJson = res.replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)```$", "").trim();
                        Voucher voucher = objectMapper.readValue(cleanJson, Voucher.class);

                        Flux<Category> categoryFlux = categoryForVoucherPort.findCategoryForVoucher(userId,
                                        voucher.getExpenseCategory());
                        Flux<Account> accountFlux = accoutsForVoucherPort
                                        .findAllMyAccountsAndType(userId, voucher.getPaymentMethod())
                                        .switchIfEmpty(accoutsForVoucherPort.findAllMyAccounts(userId));

                        return Mono.zip(categoryFlux
                                        .map(c -> new CategoryVoucher(c.getId(), c.getName(), c.getType().name()))
                                        .collectList(),
                                        accountFlux.map(a -> new AccountVoucher(a.getId(), a.getName(),
                                                        a.getType().name())).collectList())
                                        .map(tuple -> {
                                                voucher.setCategory(tuple.getT1());
                                                voucher.setAccounts(tuple.getT2());
                                                return voucher;
                                        });
                } catch (JsonProcessingException e) {
                        return Mono.error(new IllegalArgumentException(
                                        "No se pudo mapear la respuesta OCR: " + e.getMessage()));
                }
        }
}
