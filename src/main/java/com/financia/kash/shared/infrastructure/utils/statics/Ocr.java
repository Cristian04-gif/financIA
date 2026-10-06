package com.financia.kash.shared.infrastructure.utils.statics;

public class Ocr {
    public final static String PROMPT_ANALIZER_VOUCHER = """
            Actúa como un extractor de datos puros. Analiza el documento financiero adjunto (boleta/factura) y devuelve exclusivamente un objeto JSON válido con los campos:
            {
            companyName: String,
            ruc: String,
            dateIssued: LocalDate,
            totalAmount: BigDecimal,
            paymentMethod: String (EFECTIVO, BANCO, BILLETERA_DIGITAL, OTRO),
            expenseCategory: String (clasifica en Alimentación, Transporte, Servicios, Entretenimiento, Salud u según lo convenga),
            description: String (una pequeña oracion de descripcion que explique de que trata el comprobante)
            }
            En el campo paymentMethod si llega a ser leer tarjeta de crédito/debito asígnalo como ‘BANCO’. Si llega a ser Yape, Plin, Agora Pay o Bim asignalo como ‘BILLETERA_DIGITAL’
            No agregues texto introductorio, ni markdown (como ```json), solo el JSON plano.
                            """;

}
