package com.financia.kash.movimiento.comprobante.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.comprobante.application.port.input.OcrUseCase;
import com.financia.kash.movimiento.comprobante.application.port.output.AccoutsForVoucherPort;
import com.financia.kash.movimiento.comprobante.application.port.output.CategoryForVoucherPort;
import com.financia.kash.movimiento.comprobante.domain.model.AccountVoucher;
import com.financia.kash.movimiento.comprobante.domain.model.CategoryVoucher;
import com.financia.kash.movimiento.comprobante.domain.model.Voucher;
import com.financia.kash.movimiento.comprobante.infrastructure.config.AiCascadeProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

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
        public Mono<Voucher> OcrProcess(UUID userId, byte[] imageByte, MimeType mimeType) {
                List<String> list = aiCascadeProperties.getModelsCascade();
                return Flux.fromIterable(list)
                                .concatMap(modelName -> {
                                        log.info("Se prueba con el modelo AI: {}", modelName);
                                        Resource freshResource = new ByteArrayResource(imageByte);
                                        return ocrIA(userId, freshResource, modelName, mimeType)
                                                        .onErrorResume(e -> {
                                                                log.warn("Fallo con el modelo {}. Buscando el siguiente respaldo en el YAML.... Error: {}",
                                                                                modelName, e.getMessage());
                                                                return Mono.empty();
                                                        });
                                }).next().switchIfEmpty(Mono.error(new IllegalArgumentException(
                                                "Error: Todos los modelos listados en tu application.yml han superado sus límites de cuota diarios.")))
                                .flatMap(res -> mapeo(userId, res));

        }

        private Mono<String> ocrIA(UUID userId, Resource freshResource, String modelName, MimeType mimeType) {
                log.info("Se uso el modelo AI: {}", modelName);
                String prompt = """
                                Actúa como un extractor de datos puros. Analiza el documento financiero adjunto (boleta/factura) y devuelve exclusivamente un objeto JSON válido con los campos:
                                {
                                companyName: String,
                                ruc: String,
                                dateIssued: LocalDate,
                                totalAmount: BigDecimal,
                                paymentMethod: String (EFECTIVO, BANCO, BILLETERA_DIGITAL, OTRO),
                                expenseCategory: String (clasifica en Alimentación, Transporte, Servicios, Entretenimiento, Salud u según lo convenga),
                                description: String (una pequeña oracion de descripcion que explique de que trata el comprobante)
                                }
                                En el campo paymentMethod si llega a ser leer tarjeta de crédito/debito asígnalo como ‘BANCO’. Si llega a ser Yape, Plin, Agora Pay o Bim asignalo como ‘BILLETERA_DIGITAL’
                                No agregues texto introductorio, ni markdown (como ```json), solo el JSON plano.
                                                """;

                return Mono.fromSupplier(() -> chatClient.prompt()
                                .options(GoogleGenAiChatOptions.builder().model(modelName))
                                .user(userSepc -> userSepc.text(prompt).media(mimeType,
                                                freshResource))
                                .call()
                                .content());
        };

        private Mono<Voucher> mapeo(UUID userId, String res) {
                log.info("respuesta de la IA recibida: {}", res);
                Voucher voucher = objectMapper.readValue(res,
                                Voucher.class);
                // voucher.setImage(file);

                Flux<Category> categoryFlux = categoryForVoucherPort
                                .findCategoryByNameAndUserId(userId, voucher.getExpenseCategory())
                                .switchIfEmpty(categoryForVoucherPort.findAllGlobalsAndByuserId(userId));
                Flux<Account> accountFlux = accoutsForVoucherPort
                                .findAllMyAccountsAndType(userId,
                                                voucher.getPaymentMethod())
                                .switchIfEmpty(accoutsForVoucherPort.findAllMyAccounts(userId));

                return Mono.zip(
                                categoryFlux.map(
                                                category -> new CategoryVoucher(
                                                                category.getId(),
                                                                category.getName(),
                                                                category.getType()
                                                                                .name()))
                                                .collectList(),
                                accountFlux.map(
                                                account -> new AccountVoucher(
                                                                account.getId(),
                                                                account.getName(), account.getType().name()))
                                                .collectList())
                                .map(tuple -> {
                                        List<CategoryVoucher> categories = tuple
                                                        .getT1();
                                        List<AccountVoucher> accounts = tuple
                                                        .getT2();

                                        voucher.setCategory(categories);
                                        voucher.setAccounts(accounts);
                                        return voucher;
                                });

        }

}
