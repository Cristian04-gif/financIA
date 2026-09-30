package com.financia.kash.movimiento.suscripcion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.exception.FrequencyNotFoundException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
public class Subscription {

    private final UUID id;
    private final UUID userId;
    private final UUID payingAccountId;
    private final UUID categoryId;
    private String name;
    private BigDecimal amount;
    private SubscriptionFrequency frequency;
    private Integer payDay;
    private LocalDate dateNextPayment;
    private Boolean active;

    public static Subscription create(UUID userId, UUID payingAccountId, UUID categoryId, String name,
            BigDecimal amount,
            SubscriptionFrequency frequency,
            Integer payDay) {
        existType(frequency);
        amountValidated(amount);
        LocalDate nextPay = setPaymentDate(payDay, frequency);
        return new Subscription(null, userId, payingAccountId, categoryId, name, amount, frequency, payDay, nextPay,
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
                LocalDate nextPay = today.plusMonths(1);
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
