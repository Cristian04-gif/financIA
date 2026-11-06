package com.financia.kash.movimiento.suscripcion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.suscripcion.domain.enums.SubscriptionFrequency;
import com.financia.kash.movimiento.suscripcion.domain.exception.FrequencyNotFoundException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.Getter;

@Getter
public class Subscription {

    private final UUID id;
    private final UUID userId;
    private final UUID payingAccountId;
    private final UUID categoryId;
    private String name;
    private BigDecimal amount;
    private SubscriptionFrequency frequency;
    private TypeMovement type;
    private Integer payDay;
    private LocalDate dateNextPayment;
    private Boolean active;

    public static Subscription create(UUID userId, UUID payingAccountId, UUID categoryId, String name,
            BigDecimal amount,
            SubscriptionFrequency frequency,
            TypeMovement type,
            Integer payDay) {
        existType(frequency);
        amountValidated(amount);
        LocalDate nextPay = setPaymentDate(payDay, frequency);
        return new Subscription(null, userId, payingAccountId, categoryId, name, amount, frequency, type, payDay,
                nextPay,
                true);
    }

    public void assignFrequency(SubscriptionFrequency frequency) {
        if (!existType(frequency)) {
            throw new FrequencyNotFoundException();
        }
        this.frequency = frequency;
    }

    public static void amountValidated(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El importe de la movimiento debe ser positivo");
        }
    }

    private static boolean existType(SubscriptionFrequency frequency) {
        if (frequency == null) {
            return false;
        }
        return Arrays.stream(SubscriptionFrequency.values()).anyMatch(e -> e.equals(frequency));
    }

    public void changeStatus() {
        this.active = !this.active;
    }

    public static LocalDate setPaymentDate(Integer payDay, SubscriptionFrequency frequency) {

        LocalDate today = LocalDate.now();

        switch (frequency) {
            case MENSUAL -> {
                LocalDate nextPay = null;
                if (today.getDayOfMonth() < payDay) {
                    nextPay = today.plusMonths(1);
                } else {
                    nextPay = LocalDate.of(today.getYear(), today.getMonth(), payDay);
                }
                int day = Math.min(payDay, nextPay.lengthOfMonth());
                return nextPay.withDayOfMonth(day);
            }

            case ANUAL -> {
                LocalDate nextPay = today.plusYears(1);
                int day = Math.min(payDay, nextPay.lengthOfMonth());
                return nextPay.withDayOfMonth(day);
            }

            case SEMANAL -> {
                LocalDate nextPay = today.plusWeeks(1);
                int day = Math.min(payDay, nextPay.lengthOfMonth());
                return nextPay.withDayOfMonth(day);
            }

            default -> throw new FrequencyNotFoundException();
        }
    }

    @Default
    @JsonCreator
    public Subscription(
            @JsonProperty("id") UUID id,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("payingAccountId") UUID payingAccountId,
            @JsonProperty("categoryId") UUID categoryId,
            @JsonProperty("name") String name,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("frequency") SubscriptionFrequency frequency,
            @JsonProperty("type") TypeMovement type,
            @JsonProperty("payDay") Integer payDay,
            @JsonProperty("dateNextPayment") LocalDate dateNextPayment,
            @JsonProperty("active") Boolean active) {
        this.id = id;
        this.userId = userId;
        this.payingAccountId = payingAccountId;
        this.categoryId = categoryId;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.type = type;
        this.payDay = payDay;
        this.dateNextPayment = dateNextPayment;
        this.active = active;
    }

    public void setNextPayDay() {
        switch (this.frequency) {
            case MENSUAL -> {
                LocalDate nextPay = this.dateNextPayment.plusMonths(1);
                int day = Math.min(this.payDay, nextPay.lengthOfMonth());
                this.dateNextPayment = nextPay.withDayOfMonth(day);
            }

            case ANUAL -> {
                LocalDate nextPay = this.dateNextPayment.plusYears(1);
                int day = Math.min(this.payDay, nextPay.lengthOfMonth());
                this.dateNextPayment = nextPay.withDayOfMonth(day);
            }

            case SEMANAL -> {
                LocalDate nextPay = this.dateNextPayment.plusWeeks(1);
                int day = Math.min(this.payDay, nextPay.lengthOfMonth());
                this.dateNextPayment = nextPay.withDayOfMonth(day);
            }
        }
    }

    /*
     * suscripcion
     * -------------------------
     * id
     * usuario_id
     * nombre
     * monto
     * periodicidad
     * fecha_inicio
     * proxima_fecha
     * fecha_fin
     * estado
     * 
     */
}
