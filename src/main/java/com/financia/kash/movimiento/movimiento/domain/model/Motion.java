package com.financia.kash.movimiento.movimiento.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.exception.InvalidMovementTypeException;
import com.financia.kash.movimiento.movimiento.domain.exception.MovementAlreadyActivatedException;
import com.financia.kash.movimiento.movimiento.domain.exception.MovementAlreadyDeactivatedException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
public class Motion {

    private final UUID id;
    private final UUID userId;
    private final UUID accountId;
    private final UUID categoryId;
    private TypeMovement type;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private boolean common;
    private final LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public Motion(UUID userId, UUID accountId, UUID categoryId, TypeMovement type, BigDecimal amount,
            LocalDate date, String description, boolean common) {
        this.id = null;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.common = common;
        this.creationDate = LocalDateTime.now();
        this.updateDate = null;
    }

    public void validateTransactionType(TypeMovement typeMovement) {
        if (!existType(typeMovement)) {
            throw new InvalidMovementTypeException(typeMovement.name());
        }
    }

    private boolean existType(TypeMovement typeMoviment) {
        if (typeMoviment == null) {
            return false;
        }
        return Arrays.stream(TypeMovement.values()).anyMatch(e -> e.equals(typeMoviment));
    }

    public void deactiveCommontMovement() {
        if (!this.common) {
            throw new MovementAlreadyDeactivatedException(this.id);
        }
        this.common = false;
        updateDate();
    }

    public void activeCommontMovement(UUID movementId) {
        if (this.common) {
            throw new MovementAlreadyActivatedException();
        }
        this.common = true;
        updateDate();
    }

    private void updateDate() {
        this.updateDate = LocalDateTime.now();
    }

}
