package com.financia.kash.meta.application.port.input.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.meta.domain.enums.ObjectiveState;

public record ObjectiveBelongs(UUID id,
        String name,
        String description,
        BigDecimal currentAmount,
        BigDecimal targetAmount,
        Double percentageProgress,
        ObjectiveState status) {

}
