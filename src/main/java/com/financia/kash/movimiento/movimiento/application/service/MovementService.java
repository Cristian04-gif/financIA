package com.financia.kash.movimiento.movimiento.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.movimiento.application.port.input.ChangeStateCommonMotion;
import com.financia.kash.movimiento.movimiento.application.port.input.CreateMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.DeleteMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.GetMovementUseCase;
import com.financia.kash.movimiento.movimiento.application.port.output.AccountForMovementPort;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.application.port.output.SaveAccountForMovementPort;
import com.financia.kash.movimiento.movimiento.application.port.output.UserForMovementPort;
import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.exception.UserInactiveException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementService
        implements CreateMovimentUseCase, ChangeStateCommonMotion, DeleteMovimentUseCase, GetMovementUseCase {

    private final MovementRepositoryPort movementRepositoryPort;
    private final AccountForMovementPort accountForMovementPort;
    private final SaveAccountForMovementPort saveAccountForMovementPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final UserForMovementPort userForMovementPort;

    @Override
    public Mono<PaginationResponse<Motion>> getAllMovements(UUID userId, PaginationRequest request) {
        return movementRepositoryPort.findAllMyMotions(userId, request);
    }

    @Override
    public Mono<Motion> getMovementById(UUID movementId) {
        return movementRepositoryPort.findById(movementId);
    }

    @Override
    @Transactional
    public Mono<Motion> createMotion(UUID userId, UUID accountId, UUID categoryId, TypeMovement type, BigDecimal amount,
            LocalDate date, String description, boolean common) {
        userForMovementPort.isUserActive(userId).map(isactive -> {
            if (!isactive) {
                return Mono.error(new UserInactiveException());
            }
            return Mono.just(true);
        });

        categoryRepositoryPort.existsById(categoryId).map(exist -> {
            if (!exist) {
                return Mono.error(new CategoryNotFoundException(categoryId));
            }
            return Mono.just(true);
        });

        return accountForMovementPort.findMyAccountById(accountId).flatMap(account -> {
            account.validateAccountIsActive();
            account.validateSufficientFunds(amount);
            account.transfer(amount);

            Motion motion = new Motion(userId, accountId, categoryId, type, amount, date,
                    description, common);
            motion.validateTransactionType(type);
            return movementRepositoryPort.save(motion);
        });

    }

    @Override
    public Mono<Void> deactivateCommonMovement(UUID userId, UUID movementId) {
        userForMovementPort.isUserActive(userId).map(isactive -> {
            if (!isactive) {
                return Mono.error(new UserInactiveException());
            }
            return Mono.just(true);
        });
        return movementRepositoryPort.findById(movementId).flatMap(motion -> {
            motion.deactiveCommontMovement();
            return movementRepositoryPort.save(motion);

        }).then();
    }

    @Override
    public Mono<Void> activateCommonMovement(UUID userId, UUID movementId) {
        userForMovementPort.isUserActive(userId).map(isactive -> {
            if (!isactive) {
                return Mono.error(new UserInactiveException());
            }
            return Mono.just(true);
        });
        return movementRepositoryPort.findById(movementId).flatMap(motion -> {
            motion.activeCommontMovement();
            return movementRepositoryPort.save(motion);

        }).then();
    }

    @Override
    public Mono<Void> deleteMovement(UUID movementId) {
        return movementRepositoryPort.findById(movementId).flatMap(motion -> {
            accountForMovementPort.findMyAccountById(motion.getAccountId())
                    .flatMap(account -> {
                        account.receive(motion.getAmount());
                        return saveAccountForMovementPort.save(account);
                    });

            return movementRepositoryPort.delete(movementId);

        });
    }

}
