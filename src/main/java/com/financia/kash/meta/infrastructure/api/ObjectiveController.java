package com.financia.kash.meta.infrastructure.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.meta.application.port.input.ContributionObjectiveUseCase;
import com.financia.kash.meta.application.port.input.CreateObjectiveUseCase;
import com.financia.kash.meta.application.port.input.RetriveObjectivesUseCase;
import com.financia.kash.meta.application.port.input.UpdateStatusObjectiveUseCase;
import com.financia.kash.meta.application.port.input.command.ContributionObjectiveCommand;
import com.financia.kash.meta.application.port.input.command.CreateObjectiveCommand;
import com.financia.kash.meta.application.port.input.command.UpdateStatusCommand;
import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.meta.infrastructure.api.dto.ContributionObjectiveRequest;
import com.financia.kash.meta.infrastructure.api.dto.CreateObjectiveRequest;
import com.financia.kash.meta.infrastructure.api.dto.UpdateObjectiveRequest;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/objectives")
@RequiredArgsConstructor
@Tag(name = "Metas Financieras", description = "Operaciones de la API de Metas Financieras")
public class ObjectiveController {

    private final RetriveObjectivesUseCase retriveObjectivesUseCase;
    private final CreateObjectiveUseCase createObjectiveUseCase;
    private final UpdateStatusObjectiveUseCase updateStatusObjectiveUseCase;
    private final ContributionObjectiveUseCase contributionObjectiveUseCase;

    @Operation(summary = "Metas del usuario", description = "Devuelve las metas que el usuario quiere lograr")
    @GetMapping("/my-objectives")
    public Mono<ResponseEntity<PaginationResponse<FinancialObjectives>>> getAllMyObjectives(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam(required = false, defaultValue = "0") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        PaginationRequest paginationRequest = new PaginationRequest(pageNum, pageSize, sortBy, direction);
        return retriveObjectivesUseCase.getAllObjecives(user.getId(), paginationRequest).map(ResponseEntity::ok);
    }

    @Operation(summary = "Meta del usuario", description = "Devuelve una meta por su id")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FinancialObjectives>> getById(@PathVariable UUID id) {
        return retriveObjectivesUseCase.getById(id).map(ResponseEntity::ok);
    }

    @Operation(summary = "Registra una nueva meta")
    @PostMapping()
    public Mono<ResponseEntity<FinancialObjectives>> save(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid CreateObjectiveRequest request) {
        CreateObjectiveCommand command = new CreateObjectiveCommand(user.getId(), request.name(), request.description(),
                request.targetAmount(), request.targetDate());

        return createObjectiveUseCase.createObjective(command)
                .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value));
    }

    @Operation(summary = "Estado de la meta", description = "Actualiza el estado de la meta")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateStatus(@PathVariable UUID id,
            @RequestBody @Valid UpdateObjectiveRequest request) {
        return updateStatusObjectiveUseCase.updateStatus(new UpdateStatusCommand(id, request.objectiveState()))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Registra los aportes que hace el usuario a una meta")
    @PostMapping("/contribution/{id}")
    public Mono<ResponseEntity<Void>> contributionObjective(@PathVariable UUID id,
            @RequestBody ContributionObjectiveRequest request) {
        ContributionObjectiveCommand command = new ContributionObjectiveCommand(id, request.accountId(),
                request.categoryId(), request.amount(), request.description());
        return contributionObjectiveUseCase.contributionObjective(command).map(ResponseEntity::ok);
    }

}
