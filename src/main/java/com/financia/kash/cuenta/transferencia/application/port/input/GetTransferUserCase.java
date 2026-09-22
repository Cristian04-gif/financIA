package com.financia.kash.cuenta.transferencia.application.port.input;

import java.util.UUID;

import com.financia.kash.cuenta.transferencia.domain.model.TransferDTO;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface GetTransferUserCase {
    Mono<PaginationResponse<TransferDTO>> getAllMyTransfers(UUID userId, PaginationRequest request);

    Mono<PaginationResponse<TransferDTO>> getAllTransfersByAccount(UUID accountId, PaginationRequest request);

    Mono<TransferDTO> getMyTransfer(UUID transferId);
}
