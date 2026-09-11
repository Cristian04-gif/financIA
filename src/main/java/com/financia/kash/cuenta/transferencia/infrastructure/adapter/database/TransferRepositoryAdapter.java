package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.financia.kash.cuenta.transferencia.application.port.output.TransferRepositoryPort;
import com.financia.kash.cuenta.transferencia.domain.exception.TransferNotFoundException;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.entity.TransferEntity;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.mapping.TransferMapper;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.TransferEntityRepository;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TransferRepositoryAdapter implements TransferRepositoryPort {

    private final TransferEntityRepository transferRepository;
    private final TransferMapper transferMapper;

    @Override
    public Mono<PaginationResponse<Transfer>> findAllMyTransfer(UUID userId, PaginationRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getPageSize(),
                Sort.by(request.getDirection().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        request.getSortBy()));

        Flux<TransferEntity> pageTransfer = transferRepository.findAllByUserId(userId, pageRequest);
        Mono<Long> count = transferRepository.count();
        return Mono.zip(pageTransfer.map(transferMapper::mapToDomain).collectList(), count).map(tuple -> {
            List<Transfer> transfers = tuple.getT1();
            long totalElements = tuple.getT2();
            int totalPages = (int) Math.ceil((double) totalElements / request.getPageSize());
            boolean isLats = request.getPageNum() >= Math.max(0, totalPages - 1);

            return new PaginationResponse<>(transfers, request.getPageNum(),
                    request.getPageSize(), totalPages, totalElements, isLats);

        });
    }

    @Override
    public Mono<Transfer> findById(UUID id) {
        return transferRepository.findById(id).map(transferMapper::mapToDomain)
                .switchIfEmpty(Mono.error(new TransferNotFoundException(id)));
    }

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return Mono.just(transferMapper.mapToEntity(transfer)).flatMap(entity -> transferRepository.save(entity))
                .map(transferMapper::mapToDomain);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return transferRepository.deleteById(id);
    }

}
