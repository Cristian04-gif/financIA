package com.financia.kash.cuenta.transferencia.infrastructure.database;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.exception.TransferNotFoundException;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity;
import com.financia.kash.cuenta.transferencia.infrastructure.database.mapping.TransferMapper;
import com.financia.kash.cuenta.transferencia.infrastructure.database.repository.TransferEntityRepository;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransferRepositoryAdapter implements TransferRepositoryPort {

    private final TransferEntityRepository transferRepository;
    private final TransferMapper transferMapper;

    @Override
    public PaginationResponse<Transfer> findAllMyTransfer(UUID userId, PaginationRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getPageSize(),
                Sort.by(request.getDirection().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        request.getSortBy()));

        Page<TransferEntity> pageTransfer = transferRepository.findAllByUserId(userId, pageRequest);
        List<Transfer> entities = pageTransfer.getContent().stream().map(transferMapper::mapToDomain).toList();

        return new PaginationResponse<>(entities, pageTransfer.getNumber(),
                pageTransfer.getSize(), pageTransfer.getTotalPages(), pageTransfer.getTotalElements(),
                pageTransfer.isLast());
    }

    @Override
    public Transfer findById(UUID id) {
        return transferRepository.findById(id).map(transferMapper::mapToDomain)
                .orElseThrow(() -> new TransferNotFoundException(id));
    }

    @Override
    public Transfer save(Transfer transfer) {
        TransferEntity entity = transferMapper.mapToEntity(transfer);
        TransferEntity save = transferRepository.save(entity);
        return transferMapper.mapToDomain(save);
    }

    @Override
    public void delete(UUID id) {
        transferRepository.deleteById(id);
    }

}
