package com.financia.kash.cuenta.transferencia.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

public interface TransferRepositoryPort {
    PaginationResponse<Transfer> findAllMyTransfer(UUID userId, PaginationRequest request);

    Transfer findById(UUID id);

    Transfer save(Transfer transfer);

    void delete(UUID id);
}
