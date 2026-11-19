package com.financia.kash.chatIa.domain.model;

import java.util.List;

import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.diagnosis.FinancialDiagnosis;
import com.financia.kash.analisis.domain.model.finging.FinancialFindings;

public record FinancialChatContext(
        List<FinancialChatMessage> history,
        FinancialSummary summary,
        FinancialFindings findings,
        FinancialDiagnosis diagnosis) {

}
