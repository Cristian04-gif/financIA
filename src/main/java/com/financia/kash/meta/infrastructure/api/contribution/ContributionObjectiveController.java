package com.financia.kash.meta.infrastructure.api.contribution;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.meta.application.port.input.RetriveContributionUseCase;
import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/objectives/contributions")
@RequiredArgsConstructor
@Tag(name = "Contribuciones a las Metas Financieras", description = "Operaciones de la API de Contribuciones a las Metas")
public class ContributionObjectiveController {

    private final RetriveContributionUseCase retriveContributionUseCase;

    @Operation(summary = "Contribuciones a las metas financieras")
    @GetMapping("/objective/{id}")
    public Mono<ResponseEntity<PaginationResponse<ContributionDTO>>> getAllByObjective(@PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "0") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        PaginationRequest paginationRequest = new PaginationRequest(pageNum, pageSize, sortBy, direction);
        return retriveContributionUseCase.getAllByObjective(id, paginationRequest).map(ResponseEntity::ok);
    }

    @Operation(summary = "Una contribucion a una meta")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ContributionDTO>> getById(@PathVariable UUID id) {
        return retriveContributionUseCase.getById(id).map(ResponseEntity::ok);
    }

}
