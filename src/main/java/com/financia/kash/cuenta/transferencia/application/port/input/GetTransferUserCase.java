package com.financia.kash.cuenta.transferencia.application.port.input;

import java.util.UUID;

import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

public interface GetTransferUserCase {
    PaginationResponse<Transfer> getAllMyTransfers(UUID userId, PaginationRequest request);

    Transfer getMyTransfer(UUID transferId);
}
