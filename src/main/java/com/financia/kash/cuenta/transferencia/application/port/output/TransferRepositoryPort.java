package com.financia.kash.cuenta.transferencia.application.port.output;

import java.util.UUID;

import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.domain.model.TransferDTO;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface TransferRepositoryPort {
    Mono<PaginationResponse<TransferDTO>> findAllMyTransfer(UUID userId, PaginationRequest request);

    Mono<PaginationResponse<TransferDTO>> findAllTransferByAccount(UUID account, PaginationRequest request);

    Mono<Transfer> findById(UUID id);

    Mono<TransferDTO> findByIdDTO(UUID id);

    Mono<Transfer> save(Transfer transfer);

    Mono<Void> delete(UUID id);

}
