package com.financia.kash.transaccion.application.service;

import java.util.List;
import java.util.UUID;

import com.financia.kash.transaccion.application.port.input.ManageTransactionsUseCase;
import com.financia.kash.transaccion.application.port.output.TransactionRepositoryPort;
import com.financia.kash.transaccion.domain.model.Transaction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionService implements ManageTransactionsUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepositoryPort.save(transaction);
    }

    @Override
    public Transaction findById(UUID id) {
        return transactionRepositoryPort.findById(id);
    }

    @Override
    public List<Transaction> findByUserId(UUID userId) {
        return transactionRepositoryPort.findByUserId(userId);
    }

    @Override
    public void delete(UUID id) {
        transactionRepositoryPort.delete(id);
    }
}
