package com.financia.kash.cuenta.transferencia.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.cuenta.cuenta.application.port.output.AccountRespotoryPort;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.transferencia.application.port.input.DeleteTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.input.GetTransferUserCase;
import com.financia.kash.cuenta.transferencia.application.port.input.UpdateTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService implements GetTransferUserCase, UpdateTransferUseCase, DeleteTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;
    private final AccountRespotoryPort accountRespotoryPort;

    @Override
    public PaginationResponse<Transfer> getAllMyTransfers(UUID userId, PaginationRequest request) {
        return transferRepositoryPort.findAllMyTransfer(userId, request);
    }

    @Override
    public Transfer getMyTransfer(UUID transferId) {
        return transferRepositoryPort.findById(transferId);
    }

    @Override
    @Transactional
    public void updateTransfer(UUID transferId, BigDecimal newAmount, String newDescription) {
        Transfer transfer = transferRepositoryPort.findById(transferId);

        if (newAmount != null || !transfer.getAmount().equals(newAmount)) {
            BigDecimal previousAmount = transfer.getAmount();

            Account accountOrigin = accountRespotoryPort.findMyAccountById(transfer.getSourceAccount());
            Account accountDestination = accountRespotoryPort.findMyAccountById(transfer.getDestinationAccount());

            // devuelve lo transferido anteriormente
            accountDestination.transfer(previousAmount);
            accountOrigin.receive(previousAmount);

            // corregir transferencia
            accountOrigin.transfer(newAmount);
            accountDestination.receive(newAmount);

            transfer.changeAmount(newAmount);

            accountRespotoryPort.save(accountOrigin);
            accountRespotoryPort.save(accountDestination);
        }

        if (newDescription != null || !transfer.getDescription().equals(newDescription)) {
            transfer.changeDescription(newDescription);
        }
        transferRepositoryPort.save(transfer);
    }

    @Override
    @Transactional
    public void deleteTransfer(UUID transferId) {
        Transfer transfer = transferRepositoryPort.findById(transferId);

        Account accountOrigin = accountRespotoryPort.findMyAccountById(transfer.getSourceAccount());
        Account accountDestination = accountRespotoryPort.findMyAccountById(transfer.getDestinationAccount());

        BigDecimal previousAmount = transfer.getAmount();

        accountDestination.transfer(previousAmount);
        accountOrigin.receive(previousAmount);

        accountRespotoryPort.save(accountOrigin);
        accountRespotoryPort.save(accountDestination);

        transferRepositoryPort.delete(transferId);

    }

}
