package com.financia.kash.shared.infrastructure.utils.statics;

import java.util.UUID;

public class CacheKeys {

    // account
    private final static String ALL_USER_ACCOUNTS = "accounts:user:%s";
    private final static String ACCOUNT = "account:%s";

    public static String allUserAccounts(UUID userId) {
        return String.format(ALL_USER_ACCOUNTS, userId);
    }

    public static String account(UUID accountId) {
        return String.format(ACCOUNT, accountId);
    }
}
