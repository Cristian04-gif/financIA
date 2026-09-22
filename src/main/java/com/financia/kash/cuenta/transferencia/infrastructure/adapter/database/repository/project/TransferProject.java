package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferProject(
                UUID id,
                UUID cuenta_origen_id,
                String nombre_o,
                UUID cuenta_destino_id,
                String nombre_d,
                String descripcion,
                LocalDate fecha_creacion,
                BigDecimal monto) {

}
