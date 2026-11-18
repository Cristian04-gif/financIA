package com.financia.kash.analisis.infrastructure.adapter;

import java.time.Duration;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.analisis.application.port.output.FinancialRecommendationAIPort;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialAnalysisContext;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialRecommendations;
import com.financia.kash.movimiento.comprobante.infrastructure.config.AiCascadeProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Log4j2
public class GeminiFinancialRecommendationAdapter implements FinancialRecommendationAIPort {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final AiCascadeProperties aiCascadeProperties;

    @Override
    public Mono<FinancialRecommendations> generate(FinancialAnalysisContext context) {
        if (context == null) {
            return Mono.error(new IllegalArgumentException("El contexto financiero es obligatorio"));
        }
        List<String> models = aiCascadeProperties.getAnalysisModels();

        if (models == null || models.isEmpty()) {
            return Mono.error(new IllegalStateException("No hay modelos de análisis configurados"));
        }
        return Mono.fromCallable(
                () -> objectMapper.writeValueAsString(context))
                .flatMap(contextJson -> Flux.fromIterable(models)
                        .concatMap(model -> generateWithModel(
                                contextJson,
                                model,
                                context)
                                .onErrorResume(error -> {
                                    log.warn(
                                            "Falló el modelo {}: {}",
                                            model,
                                            error.getMessage());

                                    return Mono.empty();
                                }))
                        .next()
                        .switchIfEmpty(Mono.error(
                                new IllegalStateException(
                                        "No fue posible generar recomendaciones "
                                                + "con los modelos disponibles"))));
    }

    private Mono<FinancialRecommendations> generateWithModel(
            String contextJson,
            String modelName,
            FinancialAnalysisContext context) {

        return Mono.fromCallable(() -> {

            String response = chatClient.prompt()
                    .options(GoogleGenAiChatOptions.builder()
                            .model(modelName)
                            .temperature(0.2)
                            .responseMimeType("application/json"))
                    .system(buildSystemPrompt())
                    .user(contextJson)
                    .call()
                    .content();

            String cleanJson = cleanJson(response);

            FinancialRecommendations result = objectMapper.readValue(
                    cleanJson,
                    FinancialRecommendations.class);

            validateResponse(result);

            // Las fechas provienen del contexto, no de Gemini.
            return new FinancialRecommendations(
                    context.startDate(),
                    context.endDate(),
                    result.generalAssessment(),
                    result.recommendations());
        })
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(45));
    }

    private String buildSystemPrompt() {
        return """
                Eres un analista especializado en educación financiera personal.

                Tu tarea es interpretar el contexto financiero proporcionado
                y generar recomendaciones prácticas, claras y personalizadas.

                REGLAS:
                1. Utiliza exclusivamente la información del contexto.
                2. No recalcules ni inventes indicadores financieros.
                3. No inventes cantidades, porcentajes, fechas ni datos.
                4. Prioriza los problemas críticos antes que las mejoras.
                5. Si existe déficit operativo o disponibilidad negativa,
                   prioriza recuperar el equilibrio financiero.
                6. No recomiendes incrementar el ahorro si eso puede agravar
                   un déficit o comprometer el dinero disponible.
                7. Las recomendaciones deben ser concretas y realizables.
                8. No prometas resultados ni beneficios garantizados.
                9. No proporciones recomendaciones de inversión, tributarias
                   o legales.
                10. Evita repetir recomendaciones que expresen la misma idea.
                11. Genera como máximo 5 recomendaciones.
                12. Si no hay problemas relevantes, puedes proponer mejoras
                    preventivas.

                PRIORIDADES PERMITIDAS:
                HIGH, MEDIUM, LOW.

                CATEGORÍAS PERMITIDAS:
                SAVINGS, SPENDING, BUDGET, OBJECTIVE, CASH_FLOW, GENERAL.

                RESPONDE EXCLUSIVAMENTE CON UN JSON VÁLIDO,
                sin Markdown ni bloques de código.

                La estructura debe ser:

                {
                  "startDate": null,
                  "endDate": null,
                  "generalAssessment": "Evaluación general",
                  "recommendations": [
                    {
                      "code": "CODIGO_UNICO",
                      "category": "CASH_FLOW",
                      "priority": "HIGH",
                      "title": "Título",
                      "description": "Explicación del problema",
                      "action": "Acción concreta que debe realizar el usuario",
                      "expectedBenefit": "Beneficio esperado"
                    }
                  ]
                }

                Los campos startDate y endDate pueden ser null.
                """;
    }

    private String cleanJson(String response) {
        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Gemini devolvió una respuesta vacía");
        }

        String cleaned = response.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned
                    .replaceFirst("^```(?:json)?\\s*", "")
                    .replaceFirst("\\s*```$", "");
        }

        return cleaned.trim();
    }

    private void validateResponse(
            FinancialRecommendations response) {

        if (response == null) {
            throw new IllegalStateException(
                    "La respuesta de Gemini es nula");
        }

        if (response.generalAssessment() == null
                || response.generalAssessment().isBlank()) {
            throw new IllegalStateException(
                    "Gemini no proporcionó una evaluación general");
        }

        if (response.recommendations() == null) {
            throw new IllegalStateException(
                    "Gemini no proporcionó la lista de recomendaciones");
        }

        if (response.recommendations().size() > 5) {
            throw new IllegalStateException(
                    "Gemini superó el máximo de recomendaciones permitido");
        }
    }
}
