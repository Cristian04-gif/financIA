package com.financia.kash.movimiento.movimiento.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.dashboard.user.application.port.output.RetrieveAllTransactionsPort;
import com.financia.kash.movimiento.categoria.application.port.input.CreateCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.command.CreateCategoryUserCommand;
import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.model.Category;
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
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class MovementService
                implements CreateMovimentUseCase, DeleteMovimentUseCase, GetMovementUseCase,
                RetrieveAllTransactionsPort {

        private final MovementRepositoryPort movementRepositoryPort;
        private final AccountForMovementPort accountForMovementPort;
        private final SaveAccountForMovementPort saveAccountForMovementPort;
        private final CategoryRepositoryPort categoryRepositoryPort;
        private final CreateCategoryUseCase createCategoryUseCase;
        private final SaveFileVoucherUseCase saveFileVoucherUseCase;

        private final CacheService<MovementDTO> cacheService;

        private final CacheService<Movement> cacheService2;

        @Override
        public Mono<PaginationResponse<MovementDTO>> getAllMovements(AllMovementCommand command) {
                String cacheKey = CacheKeys.allUserMovements(command.userId(), command.request());
                return cacheService.retriveCachePagination(cacheKey, MovementDTO.class)
                                .switchIfEmpty(movementRepositoryPort.findAllMyMotions(command.userId(),
                                                command.request()))
                                .flatMap(mov -> cacheService.saveCachePagination(cacheKey, mov));
                // return movementRepositoryPort.findAllMyMotions(command.userId(),
                // command.request());
        }

        @Override
        public Mono<MovementDTO> getMovementById(UUID movementId) {
                String cacheKey = CacheKeys.movements(movementId);
                return cacheService.retrieveMonoCache(cacheKey, MovementDTO.class)
                                .switchIfEmpty(movementRepositoryPort.findByIdDTO(movementId))
                                .flatMap(movement -> cacheService.saveMonoCache(cacheKey, movement));
                // return movementRepositoryPort.findByIdDTO(movementId);
        }

        @Override
        @Transactional
        public Mono<Movement> createMotion(CreateMovementCommand command) {

                if (command.category().categoryId() == null) {
                        return createCategoryUseCase
                                        .createCategoryForUser(new CreateCategoryUserCommand(command.userId(),
                                                        command.category().name(),
                                                        command.category().type(),
                                                        command.category().parentCategoryId()))
                                        .flatMap(category -> saveMovement(command, category));
                }
                return categoryRepositoryPort.findById(command.category().categoryId())
                                .flatMap(category -> saveMovement(command, category));
        }

        private Mono<Movement> saveMovement(CreateMovementCommand command, Category category) {
                return accountForMovementPort.findMyAccountById(command.accountId())
                                .flatMap(account -> {
                                        account.validateAccountIsActive();
                                        account.validateSufficientFunds(command.amount());
                                        account.transfer(command.amount());

                                        Movement motion = new Movement(command.userId(),
                                                        command.accountId(),
                                                        category.getId(), null,
                                                        command.type(), command.amount(),

                                                        command.description());
                                        return movementRepositoryPort.save(motion)
                                                        .flatMap(mot -> saveAccountForMovementPort
                                                                        .save(account)
                                                                        .then(CacheKeys.clearAllCacheMovements(
                                                                                        cacheService.redisTemplate(),
                                                                                        command.userId(), mot.getId(),
                                                                                        mot.getAccountId()))
                                                                        .thenReturn(mot));
                                });
        }

        @Override
        @Transactional
        public Mono<Void> deleteMovement(UUID movementId) {
                return movementRepositoryPort.findById(movementId).flatMap(
                                movement -> accountForMovementPort.findMyAccountById(movement.getAccountId())
                                                .flatMap(account -> {
                                                        account.receive(movement.getAmount());
                                                        return saveAccountForMovementPort.save(account)
                                                                        .then(movementRepositoryPort.delete(movementId)
                                                                                        .then(CacheKeys.clearAllCacheMovements(
                                                                                                        cacheService.redisTemplate(),
                                                                                                        movement.getUserId(),
                                                                                                        movementId,
                                                                                                        movement.getAccountId())))
                                                                        .then();
                                                }));

        }

        @Override
        public Mono<Void> saveVoucherFile(SaveCoucherCommand command) {
                return movementRepositoryPort.findById(command.movementId())
                                .flatMap(movement -> command.filePart().flatMap(saveFileVoucherUseCase::saveFileVoucher)
                                                .flatMap(secureUrl -> {
                                                        movement.upReceipt(secureUrl);
                                                        return movementRepositoryPort.save(movement).then(CacheKeys
                                                                        .clearAllCacheMovements(
                                                                                        cacheService.redisTemplate(),
                                                                                        movement.getUserId(),
                                                                                        movement.getId()))
                                                                        .then();
                                                }));
        }

        // DASHBOARS
        @Override
        public Flux<Movement> retrieveAllTransactions(UUID userId, LocalDateTime from,
                        LocalDateTime until) {
                String cacheKey = CacheKeys.allMovements(userId);
                return cacheService2.getCacheList(cacheKey, Movement.class)
                                .switchIfEmpty(movementRepositoryPort.findAll(userId, from, until).collectList()
                                                .flatMapMany(movs -> cacheService2.saveListCache(cacheKey, movs)));
        }

        @Override
        public Flux<Movement> retrieveAllTransactionByCategory(UUID userId, UUID categoryId, LocalDateTime from,
                        LocalDateTime until) {
                String cacheKey = CacheKeys.allMovementsCategory(userId, categoryId);
                return cacheService2.getCacheList(cacheKey, Movement.class)
                                .switchIfEmpty(movementRepositoryPort.findAllByCategory(userId, categoryId, from, until)
                                                .collectList()
                                                .flatMapMany(movs -> cacheService2.saveListCache(cacheKey, movs)));
        }

}
