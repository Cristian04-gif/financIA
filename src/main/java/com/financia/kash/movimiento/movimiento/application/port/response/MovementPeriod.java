package com.financia.kash.movimiento.movimiento.application.port.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovementPeriod(LocalDate period, BigDecimal amount) {

}
