package com.financia.kash.movimiento.comprobante.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class Voucher {
    private String companyName;
    private String ruc;
    private LocalDate dateIssued;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String expenseCategory;
    private String description;
    private List<CategoryVoucher> category;
    private List<AccountVoucher> accounts;
}
