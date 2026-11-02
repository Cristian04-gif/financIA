package com.financia.kash.meta.application.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllGoalsPort;
import com.financia.kash.meta.application.port.input.ContributionObjectiveUseCase;
import com.financia.kash.meta.application.port.input.CreateObjectiveUseCase;
import com.financia.kash.meta.application.port.input.RetriveObjectivesUseCase;
import com.financia.kash.meta.application.port.input.UpdateStatusObjectiveUseCase;
import com.financia.kash.meta.application.port.input.command.ContributionObjectiveCommand;
import com.financia.kash.meta.application.port.input.command.CreateObjectiveCommand;
import com.financia.kash.meta.application.port.input.command.UpdateStatusCommand;
import com.financia.kash.meta.application.port.output.CategoryForObjectivesPort;
import com.financia.kash.meta.application.port.output.ContributeObjectiveRepositoryPort;
import com.financia.kash.meta.application.port.output.FinancialObjectiveRepositoryPort;
import com.financia.kash.meta.application.port.output.MovementSaveForObjective;
import com.financia.kash.meta.application.port.output.RetriveAccountForObjectivesPort;
import com.financia.kash.meta.application.port.output.RetriveUserForSObjectivesPort;
import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.meta.domain.model.ContributeObjetive;
import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.notificacion.application.service.NotificationService;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;
import com.financia.kash.shared.domain.utils.statics.NotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class FinancialObjectiveService
                implements RetriveObjectivesUseCase, CreateObjectiveUseCase, UpdateStatusObjectiveUseCase,
                ContributionObjectiveUseCase, RetrieveAllGoalsPort {

        private final FinancialObjectiveRepositoryPort financialObjectiveRepositoryPort;
        private final ContributeObjectiveRepositoryPort contributeObjectiveRepositoryPort;
        private final RetriveAccountForObjectivesPort retriveAccountForObjectives;
        private final CategoryForObjectivesPort categoryForObjectivesPort;
        private final MovementSaveForObjective movementSaveForObjective;
        private final NotificationService notificationService;
        private final CacheService<FinancialObjectives> cacheService;
        private final RetriveUserForSObjectivesPort retriveUserForSObjectivesPort;

        @Override
        public Mono<PaginationResponse<FinancialObjectives>> getAllObjecives(UUID userId, PaginationRequest request) {
                String cackeKey = CacheKeys.allObjectivesUser(userId, request);
                return cacheService.retriveCachePagination(cackeKey, FinancialObjectives.class)
                                .switchIfEmpty(financialObjectiveRepositoryPort.findAllByUserId(userId, request)
                                                .flatMap(objectives -> cacheService.saveCachePagination(cackeKey,
                                                                objectives)));
                // return financialObjectiveRepositoryPort.findAllByUserId(userId, request);
        }

        @Override
        public Mono<FinancialObjectives> getById(UUID id) {
                String cacheKey = CacheKeys.objective(id);
                return cacheService.retrieveMonoCache(cacheKey, FinancialObjectives.class)
                                .switchIfEmpty(financialObjectiveRepositoryPort.findById(id)
                                                .flatMap(objective -> cacheService.saveMonoCache(cacheKey, objective)));
                // return financialObjectiveRepositoryPort.findById(id);
        }

        @Override
        public Mono<FinancialObjectives> createObjective(CreateObjectiveCommand command) {
                return Mono.fromCallable(() -> {
                        return new FinancialObjectives(command.userId(), command.name(), command.description(),
                                        command.targetAmount(), command.targetDate());
                }).flatMap(financialObjectiveRepositoryPort::save)
                                .doOnSuccess(objective -> CacheKeys.deleteAllObjectivesUser(
                                                cacheService.redisTemplate(),
                                                objective.getUserId())
                                // return Mono.when(borrarTodo).thenReturn(objective);
                                );
        }

        @Override
        public Mono<Void> updateStatus(UpdateStatusCommand command) {
                return financialObjectiveRepositoryPort.findById(command.id()).flatMap(objective -> {
                        objective.changeStatus(command.objectiveState());
                        return financialObjectiveRepositoryPort.save(objective);
                }).doOnSuccess(objective -> CacheKeys.clearAllCacheFinancialObjectives(cacheService.redisTemplate(),
                                objective.getUserId(), objective.getId())).then();
        }

        @Override
        @Transactional
        public Mono<Void> contributionObjective(ContributionObjectiveCommand command) {
                Mono<Account> accountMono = retriveAccountForObjectives.findMyAccountById(command.accountId());
                Mono<Boolean> categoryMono = categoryForObjectivesPort.existsById(command.categoryId());
                Mono<FinancialObjectives> objectiveMono = financialObjectiveRepositoryPort
                                .findById(command.objectiveId());

                return Mono.zip(accountMono, categoryMono, objectiveMono).flatMap(tuple -> {
                        Account account = tuple.getT1();
                        FinancialObjectives objective = tuple.getT3();

                        // cuenta que hara el movimiento
                        account.validateAccountIsActive();
                        account.validateSufficientFunds(command.amount());
                        account.transfer(command.amount());

                        Movement movement = new Movement(account.getUserId(), account.getId(), command.categoryId(),
                                        null,
                                        TypeMovement.AHORRO, command.amount(), command.description());
                        return movementSaveForObjective.save(movement)
                                        .flatMap(movementSaved -> retriveAccountForObjectives.save(account)
                                                        .flatMap(accountSaved -> {

                                                                ContributeObjetive contributeObjetive = objective
                                                                                .contributeObjetive(
                                                                                                movementSaved.getId(),
                                                                                                command.amount(),
                                                                                                command.description());

                                                                return contributeObjectiveRepositoryPort
                                                                                .save(contributeObjetive)
                                                                                .then(financialObjectiveRepositoryPort
                                                                                                .save(objective))
                                                                                .doOnSuccess(e -> CacheKeys
                                                                                                .clearAllCacheFinancialObjectivesContribution(
                                                                                                                cacheService.redisTemplate(),
                                                                                                                accountSaved.getUserId(),
                                                                                                                accountSaved.getId(),
                                                                                                                objective.getId()));
                                                        }))
                                        .then();
                });

        }

        // DASHBOARD
        @Override
        public Flux<FinancialObjectives> retrieveAllGoals(UUID userId, LocalDate from, LocalDate until) {
                String cachekey = CacheKeys.allObjectives(userId);
                return cacheService.getCacheList(cachekey, FinancialObjectives.class)
                                .switchIfEmpty(financialObjectiveRepositoryPort.findAllByUser(userId, from, until)
                                                .collectList()
                                                .flatMapMany(flux -> cacheService.saveListCache(cachekey, flux)));
        }

        @Override
        public Mono<Void> updatEstatusExpiredGoals() {
                return financialObjectiveRepositoryPort.findExpired(LocalDate.now())
                                .filter(obj -> obj.getStatus().equals(ObjectiveState.EN_PROCESO)).flatMap(obj -> {
                                        obj.changeStatus(ObjectiveState.FINALIZADA);
                                        return financialObjectiveRepositoryPort.save(obj).doOnSuccess(
                                                        objective -> CacheKeys.clearAllCacheFinancialObjectives(
                                                                        cacheService.redisTemplate(),
                                                                        objective.getUserId(), objective.getId()))
                                                        .then(sendNotificacion(obj));
                                }).then();
        }

        private Mono<Void> sendNotificacion(FinancialObjectives objectives) {
                Map<String, Object> data = Map.of(
                                "objectiveId", objectives.getId(),
                                "objectiveName", objectives.getName(),
                                "currentAmount", objectives.getCurrentAmount(),
                                "targetAmount", objectives.getTargetAmount(),
                                "date", LocalDate.now());
                return retriveUserForSObjectivesPort.findMe(objectives.getUserId())
                                .doOnNext(user -> notificationService.sendToUser(user.getEmail(),
                                                NotificationMessage.sendObjectivesExpired(objectives.getName(), data)))
                                .then()
                                .onErrorResume(e -> {
                                        log.error("Error al enviar notificación de meta financiera para usuario {}: {}",
                                                        objectives.getUserId(), e.getMessage());
                                        return Mono.empty();
                                });
        }

}
