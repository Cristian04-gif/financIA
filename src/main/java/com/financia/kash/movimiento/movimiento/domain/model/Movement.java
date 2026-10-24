package com.financia.kash.movimiento.movimiento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.movimiento.domain.exception.FailureSendReceiptException;
import com.financia.kash.movimiento.movimiento.domain.exception.InvalidMovementTypeException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.Getter;

@Getter
public class Movement {

    private final UUID id;
    private final UUID userId;
    private final UUID accountId;
    private final UUID categoryId;
    private final UUID subscriptionId;
    private TypeMovement type;
    private BigDecimal amount;
    // private LocalDate date;
    private String description;
    private final LocalDateTime creationDate;
    private String receiptUrl;
    private LocalDateTime updateDate;

    public Movement(UUID userId, UUID accountId, UUID categoryId, UUID subscriptionId, TypeMovement type,
            BigDecimal amount,
            String description) {
        this.id = null;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.subscriptionId = subscriptionId;
        validateTransactionType(type);
        this.amount = amount;
        // this.date = date;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.updateDate = null;
    }

    public void validateTransactionType(TypeMovement typeMovement) {
        if (!existType(typeMovement)) {
            throw new InvalidMovementTypeException(typeMovement.name());
        }
        this.type = typeMovement;
    }

    private boolean existType(TypeMovement typeMoviment) {
        if (typeMoviment == null) {
            return false;
        }
        return Arrays.stream(TypeMovement.values()).anyMatch(e -> e.equals(typeMoviment));
    }

    public void upReceipt(String url) {
        if (url == null) {
            throw new FailureSendReceiptException();
        }
        this.receiptUrl = url;
    }

    @Default
    @JsonCreator
    public Movement(@JsonProperty("id") UUID id,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("accountId") UUID accountId,
            @JsonProperty("categoryId") UUID categoryId,
            @JsonProperty("subscriptionId") UUID subscriptionId,
            @JsonProperty("type") TypeMovement type,
            @JsonProperty("amount") BigDecimal amount,
            // @JsonProperty("date") LocalDate date,
            @JsonProperty("description") String description,
            @JsonProperty("creationDate") LocalDateTime creationDate,
            @JsonProperty("receiptUrl") String receiptUrl,
            @JsonProperty("updateDate") LocalDateTime updateDate) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.subscriptionId = subscriptionId;
        this.type = type;
        this.amount = amount;
        // this.date = date;
        this.description = description;
        this.creationDate = creationDate;
        this.receiptUrl = receiptUrl;
        this.updateDate = updateDate;
    }

}
