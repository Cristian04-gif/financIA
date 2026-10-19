package com.financia.kash.shared.domain.exception;

public class RedisException extends RuntimeException {

    public RedisException(Exception e) {
        super("Error deserializando JSON de redis " + e.getMessage());
    }

}
