package com.financia.kash.movimiento.movimiento.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.comprobante.application.port.input.SaveFileVoucherUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.CreateMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.DeleteMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.GetMovementUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.command.AllMovementCommand;
import com.financia.kash.movimiento.movimiento.application.port.input.command.CreateMovementCommand;
import com.financia.kash.movimiento.movimiento.application.port.input.command.SaveCoucherCommand;
import com.financia.kash.movimiento.movimiento.application.port.input.response.MovementDTO;
import com.financia.kash.movimiento.movimiento.application.port.output.AccountForMovementPort;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.application.port.output.SaveAccountForMovementPort;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementService
        implements CreateMovimentUseCase, DeleteMovimentUseCase, GetMovementUseCase {

    private final MovementRepositoryPort movementRepositoryPort;
    private final AccountForMovementPort accountForMovementPort;
    private final SaveAccountForMovementPort saveAccountForMovementPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final SaveFileVoucherUseCase saveFileVoucherUseCase;

    @Override
    public Mono<PaginationResponse<MovementDTO>> getAllMovements(AllMovementCommand command) {
        return movementRepositoryPort.findAllMyMotions(command.userId(), command.request());
    }

    @Override
    public Mono<MovementDTO> getMovementById(UUID movementId) {
        return movementRepositoryPort.findByIdDTO(movementId);
    }

    @Override
    @Transactional
    public Mono<Movement> createMotion(CreateMovementCommand command) {

        return categoryRepositoryPort.existsById(command.categoryId()).flatMap(exist -> {
            if (!exist) {
                return Mono.error(new CategoryNotFoundException(command.categoryId()));
            }
            return accountForMovementPort.findMyAccountById(command.categoryId()).flatMap(account -> {
                account.validateAccountIsActive();
                account.validateSufficientFunds(command.amount());
                account.transfer(command.amount());

                Movement motion = new Movement(command.userId(), command.accountId(), command.categoryId(), null,
                        command.type(), command.amount(),
                        command.date(),
                        command.description());
                return movementRepositoryPort.save(motion)
                        .flatMap(mot -> saveAccountForMovementPort.save(account).thenReturn(mot));
            });
        });

    }

    @Override
    @Transactional
    public Mono<Void> deleteMovement(UUID movementId) {
        return movementRepositoryPort.findById(movementId).flatMap(
                movement -> accountForMovementPort.findMyAccountById(movement.getAccountId()).flatMap(account -> {
                    account.receive(movement.getAmount());
                    return saveAccountForMovementPort.save(account).then(movementRepositoryPort.delete(movementId));
                }));

    }

    @Override
    public Mono<Void> saveVoucherFile(SaveCoucherCommand command) {
        return movementRepositoryPort.findById(command.movementId())
                .flatMap(movement -> command.filePart().flatMap(saveFileVoucherUseCase::saveFileVoucher)
                        .flatMap(secureUrl -> {
                            movement.upReceipt(secureUrl);
                            return movementRepositoryPort.save(movement).then();
                        }));
    }

}
