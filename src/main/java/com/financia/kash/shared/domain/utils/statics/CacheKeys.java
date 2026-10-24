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
    private final static String ALL_OBJECTIVES = "objectives:user:%s";
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

    public static String allObjectives(UUID userId) {
        return String.format(ALL_OBJECTIVES, userId);
    }

    // DELETE CACHE
    public static Mono<Long> deleteAllObjectivesUser(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match("objectives:user:%s*".formatted(userId)).build());
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
    // CATEGORY
    private final static String ALL_GLOBAL_CATEGORIES = "categories:global";
    private final static String ALL_USER_CATEGORIES = "categories:user:%s";
    private final static String CATEGORY = "category:%s";

    // CACHE KEYS
    public static String allGlobalCategories() {
        return String.format(ALL_GLOBAL_CATEGORIES);
    }

    public static String allUserCategories(UUID userId) {
        return String.format(ALL_USER_CATEGORIES, userId);
    }

    public static String category(UUID categoryId) {
        return String.format(CATEGORY, categoryId);
    }

    // DELETE CACHE
    public static Mono<Long> deleteAllGlobalCategories(ReactiveStringRedisTemplate redisTemplate) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match(ALL_GLOBAL_CATEGORIES).build());
        return redisTemplate.delete(keys);
    }

    public static Mono<Long> deleteAllUserCategories(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match(ALL_USER_CATEGORIES.formatted(userId)).build());
        return redisTemplate.delete(keys);
    }

    public static Mono<Boolean> deleteCategory(ReactiveStringRedisTemplate redisTemplate, UUID categoryId) {
        String key = category(categoryId);
        return redisTemplate.opsForValue().delete(key);
    }

    // ------------------------------------------------------------------------------------------------
    // MOVEMENTS
    private static final String ALL_MOVEMENTS = "movements:user:%s";
    private static final String ALL_MOVEMENTS_CATEGORY = "movements:user:%s:category:%s";
    private static final String ALL_USER_MOVEMENTS = "movements:user:%s:page:%d:size:%d:sort:%s:direction:%s";
    private static final String MOVEMENT = "movement:%s";

    // CACHE KEYS

    public static String allMovements(UUID userID) {
        return String.format(ALL_MOVEMENTS, userID);
    }

    public static String allMovementsCategory(UUID userId, UUID categoryId) {
        return String.format(ALL_MOVEMENTS_CATEGORY, userId, categoryId);
    }

    public static String allUserMovements(UUID userId, PaginationRequest request) {
        return String.format(ALL_USER_MOVEMENTS, userId, request.getPageNum(), request.getPageSize(),
                request.getSortBy(), request.getDirection());
    }

    public static String movements(UUID movementId) {
        return String.format(MOVEMENT, movementId);
    }

    // DELETE CACHE
    public static Mono<Long> deleteAllCacheMovementesUser(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match("movements:user:%s*".formatted(userId)).build());
        return redisTemplate.delete(keys);
    }

    private static Mono<Boolean> deleteMovement(ReactiveStringRedisTemplate redisTemplate, UUID movementId) {
        String key = movements(movementId);
        return redisTemplate.opsForValue().delete(key);
    }

    // ------------------------------------------------------------------------------------
    // SUBCRIPTION
    private static final String All_USER_SUSCRIPTION = "subscriptions:user:%s";
    private static final String SUBSCRIPTION = "subcription:%s";

    // CACHE KEYS
    public static String allUserSubscription(UUID userId) {
        return String.format(All_USER_SUSCRIPTION, userId);
    }

    public static String subscription(UUID subsId) {
        return String.format(SUBSCRIPTION, subsId);
    }

    // DELETE CACHE
    public static Mono<Long> deleteAllCacheUserSubscription(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        Flux<String> keys = redisTemplate.scan(
                ScanOptions.scanOptions().match(allUserSubscription(userId)).build());
        return redisTemplate.delete(keys);
    }

    private static Mono<Boolean> deleteIndividualSub(ReactiveStringRedisTemplate redisTemplate, UUID subId) {
        String key = subscription(subId);
        return redisTemplate.opsForValue().delete(key);
    }

    // ------------------------------------------------------------------------------------
    // USERS
    public static final String USER = "users:%s";

    // CACHE KEY
    public static String user(UUID userId) {
        return String.format(USER, userId);
    }

    public static String user(String email) {
        return String.format(USER, email);
    }

    // DELETE CACHE
    public static Mono<Boolean> deleteIndividualUser(ReactiveStringRedisTemplate redisTemplate, UUID userId) {
        String key = user(userId);
        return redisTemplate.opsForValue().delete(key);
    }

    // ------------------------------------------------------------------------------------
    // METHODS DELETE
    // ------------------------------------------------------------------------------------

    // ACCOUNTS
    public static Mono<Boolean> clearAllCacheAccounts(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID accountId) {
        Mono<Boolean> borrarIndividual = deleteInidividualAccount(redisTemplate,
                accountId);
        Mono<Long> borrarTodo = deleteAllCacheAccounts(redisTemplate,
                userId);
        return Mono.when(borrarIndividual, borrarTodo).then(Mono.just(Boolean.TRUE));
    }

    // TRANSFERS
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

    // FINANCIAL OBJECTIVES
    public static Mono<Boolean> clearAllCacheFinancialObjectives(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID objectiveId) {
        Mono<Long> borrarTodo = CacheKeys.deleteAllObjectivesUser(redisTemplate,
                userId);
        Mono<Boolean> borrarIndividual = CacheKeys.deleteIndividualObjective(
                redisTemplate,
                objectiveId);
        return Mono.when(borrarIndividual, borrarTodo).thenReturn(Boolean.TRUE);
    }

    // CONTRIBUTIONS OBJECTIVES
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

    // CATEGORIES
    public static Mono<Boolean> clearAllUserCategories(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID categoryId) {
        Mono<Long> borrarCacheUsersCategories = deleteAllUserCategories(redisTemplate, userId);
        Mono<Boolean> borrarCacheCategoryIndividual = deleteCategory(redisTemplate, categoryId);
        return Mono.when(borrarCacheCategoryIndividual, borrarCacheUsersCategories).thenReturn(Boolean.TRUE);
    }

    public static Mono<Boolean> clearAllGlobalCategories(ReactiveStringRedisTemplate redisTemplate, UUID categoryId) {
        Mono<Long> borrarCacheGlobal = deleteAllGlobalCategories(redisTemplate);
        Mono<Boolean> borrarIndividual = deleteCategory(redisTemplate, categoryId);
        return Mono.when(borrarCacheGlobal, borrarIndividual).thenReturn(Boolean.TRUE);
    }

    // MOVEMENTS
    public static Mono<Boolean> clearAllCacheMovements(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID movementId, UUID accountId) {
        Mono<Long> borrarCacheTodo = deleteAllCacheMovementesUser(redisTemplate, userId);
        Mono<Boolean> borrarIndividual = deleteMovement(redisTemplate, movementId);
        Mono<Long> borrarCuentas = deleteAllCacheAccounts(redisTemplate, userId);
        Mono<Boolean> borrarCuentaIndividual = deleteInidividualAccount(redisTemplate, accountId);
        return Mono.when(borrarCacheTodo, borrarIndividual, borrarCuentas, borrarCuentaIndividual)
                .thenReturn(Boolean.TRUE);
    }

    public static Mono<Boolean> clearAllCacheMovements(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID movementId) {
        Mono<Long> borrarCacheTodo = deleteAllCacheMovementesUser(redisTemplate, userId);
        Mono<Boolean> borrarIndividual = deleteMovement(redisTemplate, movementId);

        return Mono.when(borrarCacheTodo, borrarIndividual)
                .thenReturn(Boolean.TRUE);
    }

    // SUBSCRIPTION
    public static Mono<Boolean> clearAllCacheSubscription(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID subId, UUID accountId) {
        Mono<Long> borratTodoSubs = deleteAllCacheUserSubscription(redisTemplate, userId);
        Mono<Boolean> borrarSubIndividual = deleteIndividualSub(redisTemplate, subId);
        Mono<Long> borrarTodoMovements = deleteAllCacheMovementesUser(redisTemplate, userId);
        Mono<Long> borrarTodoacount = deleteAllCacheAccounts(redisTemplate, userId);
        Mono<Boolean> borrarIndividualAccount = deleteInidividualAccount(redisTemplate, accountId);
        return Mono.when(borratTodoSubs, borrarSubIndividual, borrarTodoMovements, borrarTodoacount,
                borrarIndividualAccount).thenReturn(Boolean.TRUE);
    }

    public static Mono<Boolean> clearCacheSubscription(ReactiveStringRedisTemplate redisTemplate, UUID userId,
            UUID subId) {
        Mono<Long> borratTodoSubs = deleteAllCacheUserSubscription(redisTemplate, userId);
        Mono<Boolean> borrarSubIndividual = deleteIndividualSub(redisTemplate, subId);
        return Mono.when(borrarSubIndividual, borratTodoSubs).thenReturn(Boolean.TRUE);
    }

}
