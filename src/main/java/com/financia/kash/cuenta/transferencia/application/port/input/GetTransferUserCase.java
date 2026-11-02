package com.financia.kash.cuenta.transferencia.application.port.input;

import java.util.UUID;

import com.financia.kash.cuenta.transferencia.application.port.input.command.GetTransferAccountCommand;
import com.financia.kash.cuenta.transferencia.application.port.input.command.GetTransferCommand;
import com.financia.kash.cuenta.transferencia.application.port.input.response.TransferDTO;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface GetTransferUserCase {
    Mono<PaginationResponse<TransferDTO>> getAllMyTransfers(GetTransferCommand command);

    Mono<PaginationResponse<TransferDTO>> getAllTransfersByAccount(GetTransferAccountCommand command);

    Mono<TransferDTO> getMyTransfer(UUID transferId);
}
