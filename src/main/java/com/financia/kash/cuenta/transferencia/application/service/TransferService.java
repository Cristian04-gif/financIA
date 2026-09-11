package com.financia.kash.cuenta.transferencia.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.transferencia.application.port.input.DeleteTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.input.GetTransferUserCase;
import com.financia.kash.cuenta.transferencia.application.port.input.UpdateTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransferService implements GetTransferUserCase, UpdateTransferUseCase, DeleteTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;
    private final AccountRespotoryPort accountRespotoryPort;

    @Override
    public Mono<PaginationResponse<Transfer>> getAllMyTransfers(UUID userId, PaginationRequest request) {
        return transferRepositoryPort.findAllMyTransfer(userId, request);
    }

    @Override
    public Mono<Transfer> getMyTransfer(UUID transferId) {
        return transferRepositoryPort.findById(transferId);
    }

    @Override
    @Transactional
    public Mono<Void> updateTransfer(UUID transferId, BigDecimal newAmount, String newDescription) {
        return transferRepositoryPort.findById(transferId).flatMap(transfer -> {
            if (newAmount != null || !transfer.getAmount().equals(newAmount)) {
                BigDecimal previousAmount = transfer.getAmount();

                Mono<Account> accountOrigin = accountRespotoryPort.findMyAccountById(transfer.getSourceAccount());
                Mono<Account> accountDestination = accountRespotoryPort
                        .findMyAccountById(transfer.getDestinationAccount());

                Mono.zip(accountOrigin, accountDestination).flatMap(tuple -> {
                    Account origin = tuple.getT1();
                    Account detination = tuple.getT2();

                    // devuelve lo transferido anteriormente
                    detination.transfer(previousAmount);
                    origin.receive(previousAmount);

                    // corregir transferencia
                    origin.transfer(newAmount);
                    detination.receive(newAmount);

                    transfer.changeAmount(newAmount);

                    return accountRespotoryPort.save(origin).then(accountRespotoryPort.save(detination));
                });
            }

            if (newDescription != null || !transfer.getDescription().equals(newDescription)) {
                transfer.changeDescription(newDescription);

            }
            return transferRepositoryPort.save(transfer);
        }).then();

    }

    @Override
    @Transactional
    public Mono<Void> deleteTransfer(UUID transferId) {
        return transferRepositoryPort.findById(transferId).flatMap(transfer -> {
            Mono<Account> accountOrigin = accountRespotoryPort.findMyAccountById(transfer.getSourceAccount());
            Mono<Account> accountDestination = accountRespotoryPort.findMyAccountById(transfer.getDestinationAccount());

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
