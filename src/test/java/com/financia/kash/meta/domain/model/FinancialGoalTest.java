package com.financia.kash.meta.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class FinancialGoalTest {

    @Test
    void contributionCompletesGoalWhenTargetIsReached() {
        FinancialGoal goal = FinancialGoal.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .name("Fondo de emergencia")
                .targetAmount(new BigDecimal("1000.00"))
                .currentAmount(new BigDecimal("700.00"))
                .targetDate(LocalDate.now().plusMonths(6))
                .status(GoalStatus.ACTIVE)
                .creationDate(LocalDate.now())
                .build();

        goal.addContribution(new BigDecimal("300.00"));

        assertEquals(new BigDecimal("0.00"), goal.remainingAmount());
        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
    }
}
