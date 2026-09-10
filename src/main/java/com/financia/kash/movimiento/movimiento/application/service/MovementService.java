package com.financia.kash.movimiento.movimiento.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.movimiento.application.port.input.ChangeStateCommonMotion;
import com.financia.kash.movimiento.movimiento.application.port.input.CreateMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.DeleteMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.GetMovementUseCase;
import com.financia.kash.movimiento.movimiento.application.port.output.AccountForMovementPort;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.application.port.output.UserForMovementPort;
import com.financia.kash.movimiento.movimiento.domain.model.Motion;
import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.exception.UserInactiveException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovementService
        implements CreateMovimentUseCase, ChangeStateCommonMotion, DeleteMovimentUseCase, GetMovementUseCase {

    private final MovementRepositoryPort movementRepositoryPort;
    private final AccountForMovementPort accountForMovementPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final UserForMovementPort userForMovementPort;

    @Override
    public PaginationResponse<Motion> getAllMovements(UUID userId, PaginationRequest request) {
        return movementRepositoryPort.findAllMyMotions(userId, request);
    }

    @Override
    public Motion getMovementById(UUID movementId) {
        return movementRepositoryPort.findById(movementId);
    }

    @Override
    @Transactional
    public Motion createMotion(UUID userId, UUID accountId, UUID categoryId, TypeMovement type, BigDecimal amount,
            LocalDate date, String description, boolean common) {
        if (!userForMovementPort.isUserActive(userId)) {
            throw new UserInactiveException();
        }
        if (!categoryRepositoryPort.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        Account account = accountForMovementPort.findMyAccountById(accountId);
        account.validateAccountIsActive();
        account.validateSufficientFunds(amount);
        account.transfer(amount);
        Motion motion = new Motion(userId, accountId, categoryId, type, amount, date, description, common);
        motion.validateTransactionType(type);
        return movementRepositoryPort.save(motion);

    }

    @Override
    public void deactivateCommonMovement(UUID userId, UUID movementId) {
        if (!userForMovementPort.isUserActive(userId)) {
            throw new UserInactiveException();
        }
        Motion motion = movementRepositoryPort.findById(movementId);
        motion.deactiveCommontMovement();
        movementRepositoryPort.save(motion);
    }

    @Override
    public void activateCommonMovement(UUID userId, UUID movementId) {
        if (!userForMovementPort.isUserActive(userId)) {
            throw new UserInactiveException();
        }
        Motion motion = movementRepositoryPort.findById(movementId);
        motion.activeCommontMovement(movementId);
        movementRepositoryPort.save(motion);
    }

    @Override
    public void deleteMovement(UUID movementId) {
        movementRepositoryPort.delete(movementId);
    }

}
