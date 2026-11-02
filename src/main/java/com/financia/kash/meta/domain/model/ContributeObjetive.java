package com.financia.kash.meta.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
public class ContributeObjetive {

    private final UUID id;
    private final UUID objectiveId;
    private final UUID movementId;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    public ContributeObjetive(UUID objectiveId, UUID movementId, BigDecimal amount,
            String description) {
        this.id = null;
        this.objectiveId = objectiveId;
        this.movementId = movementId;
        this.amount = amount;
        this.date = LocalDate.now();
        this.description = description;
    }

}
