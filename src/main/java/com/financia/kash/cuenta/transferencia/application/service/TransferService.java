package com.financia.kash.cuenta.transferencia.application.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.transferencia.application.port.input.DeleteTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.input.GetTransferUserCase;
import com.financia.kash.cuenta.transferencia.application.port.input.UpdateTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.input.command.GetTransferAccountCommand;
import com.financia.kash.cuenta.transferencia.application.port.input.command.GetTransferCommand;
import com.financia.kash.cuenta.transferencia.application.port.input.command.UpdateTransferCommand;
import com.financia.kash.cuenta.transferencia.application.port.input.response.TransferDTO;
import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.exception.RedisException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransferService implements GetTransferUserCase, UpdateTransferUseCase, DeleteTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;
    private final AccountRespotoryPort accountRespotoryPort;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<PaginationResponse<TransferDTO>> getAllMyTransfers(GetTransferCommand command) {
        String cacheKey = String.format("transfers:user:%s:page:%d:size:%d", command.userId(),
                command.request().getPageNum(),
                command.request().getPageSize());
        return redisTemplate.opsForValue().get(cacheKey)
                .doOnSubscribe(s -> System.out.println("Suscrito a GET: " + cacheKey))
                .doOnNext(json -> System.out.println("CACHE HIT: " + cacheKey))
                .doOnSuccess(json -> System.out.println("GET completado. Valor nulo: " + (json == null)))
                .doOnError(error -> System.err.println("ERROR GET: " + error.getMessage()))
                .map(json -> {
                    try {
                        // 2. Usamos TypeReference para que Jackson sepa que es una lista de Productos
                        return objectMapper.readValue(json, new TypeReference<PaginationResponse<TransferDTO>>() {
                        });
                    } catch (JsonProcessingException e) {
                        System.out.println(e);
                        throw new RedisException(e);
                    }
                }).switchIfEmpty(
                        Mono.defer(() -> transferRepositoryPort.findAllMyTransfer(command.userId(), command.request()))
                                .flatMap(res -> {
                                    try {
                                        String json = objectMapper.writeValueAsString(res);
                                        return redisTemplate.opsForValue()
                                                .set(cacheKey, json, Duration.ofMinutes(15))
                                                .doOnSuccess(result -> System.out.println(
                                                        "SET completado: " + result))
                                                .doOnError(error -> System.err.println(
                                                        "ERROR SET: " + error.getMessage()))
                                                .thenReturn(res);
                                    } catch (JsonProcessingException e) {
                                        return Mono.just(res);
                                    }
                                }));
        // return transferRepositoryPort.findAllMyTransfer(command.userId(),
        // command.request());
    }

    @Override
    public Mono<PaginationResponse<TransferDTO>> getAllTransfersByAccount(GetTransferAccountCommand command) {
        return transferRepositoryPort.findAllTransferByAccount(command.accountId(), command.request());
    }

    @Override
    public Mono<TransferDTO> getMyTransfer(UUID transferId) {
        return transferRepositoryPort.findByIdDTO(transferId);

    }

    @Override
    @Transactional
    public Mono<Void> updateTransfer(UpdateTransferCommand command) {
        return transferRepositoryPort.findById(command.transferId()).flatMap(transfer -> {
            BigDecimal previousAmount = transfer.getAmount();

            Mono<Account> accountOrigin = accountRespotoryPort.findMyAccountById(transfer.getSourceAccount());
            Mono<Account> accountDestination = accountRespotoryPort
                    .findMyAccountById(transfer.getDestinationAccount());
            return Mono.zip(accountOrigin, accountDestination).flatMap(tuple -> {
                Account origin = tuple.getT1();
                Account detination = tuple.getT2();

                if (command.newAmount() != null || !transfer.getAmount().equals(command.newAmount())) {
                    // devuelve lo transferido anteriormente
                    detination.transfer(previousAmount);
                    origin.receive(previousAmount);
                    // corregir transferencia
                    origin.transfer(command.newAmount());
                    detination.receive(command.newAmount());
                    transfer.changeAmount(command.newAmount());
                }

                if (command.newDescription() != null || !transfer.getDescription().equals(command.newDescription())) {
                    transfer.changeDescription(command.newDescription());

                }

                return accountRespotoryPort.save(origin).then(accountRespotoryPort.save(detination))
                        .then(transferRepositoryPort.save(transfer));
            });

        }).then();

    }

    @Override
    @Transactional
    public Mono<Void> deleteTransfer(UUID transferId) {
        return transferRepositoryPort.findById(transferId).flatMap(transfer -> {
            Mono<Account> accountOrigin = accountRespotoryPort.findMyAccountById(transfer.getSourceAccount());
            Mono<Account> accountDestination = accountRespotoryPort
                    .findMyAccountById(transfer.getDestinationAccount());

            return Mono.zip(accountOrigin, accountDestination).flatMap(tuple -> {
                Account origin = tuple.getT1();
                Account destination = tuple.getT2();

                BigDecimal previousAmount = transfer.getAmount();

                destination.transfer(previousAmount);
                origin.receive(previousAmount);

                accountRespotoryPort.save(origin);
                accountRespotoryPort.save(destination);

                return transferRepositoryPort.delete(transferId);
            });
        });

    }

}
