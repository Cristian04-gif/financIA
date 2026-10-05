package com.financia.kash.movimiento.comprobante.domain.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountVoucher {
    private UUID id;
    private String name;
    private String type;
}
