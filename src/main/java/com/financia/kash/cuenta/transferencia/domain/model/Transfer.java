package com.financia.kash.cuenta.transferencia.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
public class Transfer {
    private final UUID id;
    private final UUID userId;
    private final UUID sourceAccount;
    private final UUID destinationAccount;
    private BigDecimal amount;
    private String description;
    private final LocalDate creationDate;

    public Transfer(UUID userId, UUID sourceAccount, UUID destinationAccount, BigDecimal amount,
            String description) {
        this.id = null;
        this.userId = userId;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.description = description;
        this.creationDate = LocalDate.now();
    }

    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

}
