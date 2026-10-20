package com.financia.kash.shared.domain.utils.statics;

import java.util.UUID;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import com.financia.kash.shared.domain.PaginationRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CacheKeys {

    // ------------------------------------------------------------------------------------
    // ACCOUNTS
    private final static String ALL_USER_ACCOUNTS = "accounts:user:%s";
    private final static String ACCOUNT = "account:%s";

    // CACHE KEYS
    public static String allUserAccounts(UUID userId) {
        return String.format(ALL_USER_ACCOUNTS, userId);
    }

    public static String account(UUID accountId) {
        return String.format(ACCOUNT, accountId);
    }

    // DELETE CACHE
    public static Mono<Long> deleteAllCacheAccounts(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> llaves = redisTemplate.scan(
                ScanOptions.scanOptions()
                        .match(allUserAccounts(userId))
                        .build());
        return redisTemplate.delete(llaves);
    }

    private static Mono<Boolean> deleteInidividualAccount(ReactiveStringRedisTemplate redisTemplate, UUID accountId) {
        String cacheIndividual = account(accountId);
        return redisTemplate.opsForValue().delete(cacheIndividual);

    }

    // ------------------------------------------------------------------------------------
    // TRANSFERS
    private final static String ALL_TRANSFERS_USER = "transfers:user:%s:page:%d:size:%d:sort:%s:direction:%s";
    private final static String ALL_TRANSFERS_BY_ACCOUNT = "transfers:account:%s:page:%d:size:%d:sort:%s:direction:%s";
    private final static String TRANSFER = "transfer:%s";

    // CACHE KEYS

    public static String allTransfersUser(UUID userId, PaginationRequest request) {
        return String.format(ALL_TRANSFERS_USER, userId, request.getPageNum(), request.getPageSize(),
                request.getSortBy(),
                request.getDirection());
    }

    public static String allTransfersByAccount(UUID accountId, PaginationRequest request) {
        return String.format(ALL_TRANSFERS_BY_ACCOUNT, accountId, request.getPageNum(), request.getPageSize(),
                request.getSortBy(),
                request.getDirection());
    }

    public static String transfer(UUID transferId) {
        return String.format(TRANSFER, transferId);
    }

    // DELETE CACHE
    private static Mono<Long> deleteAllTranasferUser(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match("transfers:user:%s:*".formatted(userId)).build());
        return redisTemplate.delete(keys);
    }

    private static Mono<Long> deleteAllTransfersAccount(ReactiveStringRedisTemplate redisTemplate, UUID accountId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match("transfers:account:%s:*".formatted(accountId)).build());
        return redisTemplate.delete(keys);
    }

    private static Mono<Boolean> deleteIndividualTransfer(ReactiveStringRedisTemplate redisTemplate, UUID transferId) {
        String key = transfer(transferId);
        return redisTemplate.opsForValue().delete(key);
    }

    // ------------------------------------------------------------------------------------
    // FINANCIAL OBJECTIVES
    private final static String ALL_OBJECTIVES_USER = "objectives:user:%s:page:%d:size:%d:sort:%s:direction:%s";
    private final static String OBJECTIVE = "objective:%s";

    // CACHE KEYS
    public static String allObjectivesUser(UUID userId, PaginationRequest request) {
        return String.format(ALL_OBJECTIVES_USER, userId, request.getPageNum(), request.getPageSize(),
                request.getSortBy(),
                request.getDirection());
    }

    public static String objective(UUID objectiveId) {
        return String.format(OBJECTIVE, objectiveId);
    }

    // DELETE CACHE
    public static Mono<Long> deleteAllObjectivesUser(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match("objectives:user:%s:*".formatted(userId)).build());
        return redisTemplate.delete(keys);
    }

    private static Mono<Boolean> deleteIndividualObjective(ReactiveStringRedisTemplate redisTemplate,
            UUID objectiveId) {
        String key = objective(objectiveId);
        return redisTemplate.opsForValue().delete(key);
    }

    // ------------------------------------------------------------------------------------
    // CONTRIBUTION OBJECTIVES
    private final static String ALL_CONTRIBUTION_OBJECTIVE = "contribution:objective:%s:page:%d:size:%d:sort:%s:direction:%s";
    private final static String CONTRIBUTION_OBJECTIVE = "contribution:%s";

    // CACHE KEYS
    public static String allContributionObjective(UUID objectiveId, PaginationRequest request) {
        return String.format(ALL_CONTRIBUTION_OBJECTIVE, objectiveId, request.getPageNum(), request.getPageSize(),
                request.getSortBy(),
                request.getDirection());
    }

    public static String contributionObjective(UUID contirbutionId) {
        return String.format(CONTRIBUTION_OBJECTIVE, contirbutionId);
    }

    // DELETE CACHE
    private static Mono<Long> deleteAllContributionObjective(ReactiveStringRedisTemplate redisTemplate,
            UUID objectiveId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match("contribution:objective:%s:*".formatted(objectiveId)).build());

        return redisTemplate.delete(keys);
    }

    // private static Mono<Boolean>
    // deleteIndividualContribution(ReactiveStringRedisTemplate redisTemplate,
    // UUID contirbutionId) {
    // String key = contributionObjective(contirbutionId);
    // return redisTemplate.opsForValue().delete(key);
    // }

    // ------------------------------------------------------------------------------------
    // METHODS DELETE
    // ------------------------------------------------------------------------------------

    public static Mono<Boolean> clearAllCacheAccounts(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID accountId) {
        Mono<Boolean> borrarIndividual = deleteInidividualAccount(redisTemplate,
                accountId);
        Mono<Long> borrarTodo = deleteAllCacheAccounts(redisTemplate,
                userId);
        return Mono.when(borrarIndividual, borrarTodo).then(Mono.just(Boolean.TRUE));
    }

    public static Mono<Boolean> clearAllCacheTransfers(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID sourceAccountId, UUID targetAccountId, UUID transferId) {

        Mono<Long> borrarCuentasCache = deleteAllCacheAccounts(redisTemplate, userId);
        Mono<Boolean> borrarCuentaCache = deleteInidividualAccount(redisTemplate, sourceAccountId);
        Mono<Long> borrarTodoTransferCuenta = deleteAllTransfersAccount(redisTemplate, targetAccountId);

        Mono<Boolean> borrarIndividualTransfer = deleteIndividualTransfer(redisTemplate, transferId);
        Mono<Long> borrarTransferuser = deleteAllTranasferUser(redisTemplate, userId);
        return Mono
                .when(borrarCuentaCache, borrarCuentasCache, borrarIndividualTransfer, borrarTodoTransferCuenta,
                        borrarTransferuser)
                .thenReturn(Boolean.TRUE);
    }

    public static Mono<Boolean> clearAllCacheFinancialObjectives(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID objectiveId) {
        Mono<Long> borrarTodo = CacheKeys.deleteAllObjectivesUser(redisTemplate,
                userId);
        Mono<Boolean> borrarIndividual = CacheKeys.deleteIndividualObjective(
                redisTemplate,
                objectiveId);
        return Mono.when(borrarIndividual, borrarTodo).thenReturn(Boolean.TRUE);
    }

    public static Mono<Boolean> clearAllCacheFinancialObjectivesContribution(ReactiveStringRedisTemplate redisTemplate,
            UUID userId,
            UUID accountId, UUID objectiveId) {
        Mono<Long> borratTodoObjetivo = deleteAllObjectivesUser(redisTemplate, userId);
        Mono<Boolean> borrarIndividualObjetivo = deleteIndividualObjective(redisTemplate, objectiveId);
        Mono<Long> borrarTodoTransfers = deleteAllTranasferUser(redisTemplate, userId);
        Mono<Long> borrarTodoTransfersAccount = deleteAllTransfersAccount(redisTemplate, accountId);
        Mono<Long> borrarTodoAccount = deleteAllCacheAccounts(redisTemplate, accountId);
        Mono<Boolean> borrarIndividualAccount = deleteInidividualAccount(redisTemplate, accountId);
        Mono<Long> borrarTodoContribution = deleteAllContributionObjective(redisTemplate, objectiveId);
        return Mono
                .when(borrarIndividualAccount, borrarIndividualObjetivo, borrarTodoAccount, borrarTodoTransfers,
                        borrarTodoTransfersAccount,
                        borratTodoObjetivo, borrarTodoContribution)
                .doOnSuccess(e -> System.out.println("se borro todo el cache de objectives"))
                .thenReturn(Boolean.TRUE);
    }

}
