package com.financia.kash.transaccion.application.port.output;

import java.util.List;
import java.util.UUID;

import com.financia.kash.transaccion.domain.model.Transaction;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Transaction findById(UUID id);

    List<Transaction> findByUserId(UUID userId);

    void delete(UUID id);
}
