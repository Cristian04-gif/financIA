package com.financia.kash.movimiento.categoria.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.movimiento.categoria.application.port.input.CreateCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.DeleteMyCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.GetCategoriesUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.UpdateCategoryUseCase;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto.CategoryForUserRequest;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto.CategoryGlobalRequest;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto.CategoryResponse;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping.CategoryMapper;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Operaciones de la API de Categoria")
@Log4j2
public class CategoryController {

        private final GetCategoriesUseCase getCategoriesUseCase;
        private final CreateCategoryUseCase createCategoryUseCase;
        private final UpdateCategoryUseCase updateCategoryUseCase;
        private final DeleteMyCategoryUseCase deleteMyCategoryUseCase;
        private final CategoryMapper categoryMapper;

        @Operation(summary = "Categorias globales", description = "Devuelve las categorias globales")
        @GetMapping("/global")
        public ResponseEntity<Flux<CategoryResponse>> getGlobalCategories() {
                Flux<CategoryResponse> globalCategories = getCategoriesUseCase.getGlobalCategories()
                                .map(categoryMapper::mapToResponse);
                return ResponseEntity.ok(globalCategories);
        }

        @Operation(summary = "Categorias del usuario", description = "Devuelve las categorias creadas por el usuario logeado")
        @GetMapping("/of-user")
        public ResponseEntity<Flux<CategoryResponse>> getMyCategories(@AuthenticationPrincipal UserEntity user) {
                Flux<CategoryResponse> userCategories = getCategoriesUseCase.getAllMyCategory(user.getId())
                                .map(categoryMapper::mapToResponse);
                return ResponseEntity.ok(userCategories);
        }

        @Operation(summary = "Categoria", description = "Devuelve la informacion por su ID")
        @GetMapping("/category/{id}")
        public Mono<ResponseEntity<Category>> getById(@PathVariable UUID id) {
                return getCategoriesUseCase.getById(id).map(ResponseEntity::ok);
        }

        @Operation(summary = "Sub-categoria", description = "Crea una categoria que necesite usuario")
        @PostMapping("/of-user")
        public Mono<ResponseEntity<Category>> createUserCategory(@AuthenticationPrincipal UserEntity user,
                        @RequestBody CategoryForUserRequest request) {
                return createCategoryUseCase.createCategoryForUser(user.getId(), request.name(),
                                request.type(),
                                request.parentCategoryId())
                                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
        }

        @Operation(summary = "Categoria global", description = "Crea una categoria globalizada")
        @PostMapping("/global")
        public Mono<ResponseEntity<Category>> createGlobalCategory(@RequestBody CategoryGlobalRequest request) {
                return createCategoryUseCase.createMainCategory(request.name(), request.type())
                                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
        }

        @Operation(summary = "Actualizar categoria", description = "Actualiza una categoria del usuario")
        @PutMapping("/of-user/{id}")
        public Mono<ResponseEntity<Category>> updateUsercategory(@PathVariable UUID id,
                        @RequestBody CategoryForUserRequest request) {

                return updateCategoryUseCase.updateCategoryForUser(id, request.name(), request.type(),
                                request.parentCategoryId(),
                                request.active())
                                .map(category -> ResponseEntity.status(HttpStatus.ACCEPTED).body(category));

        }

        @Operation(summary = "Eliminar categoria", description = "Elimina una categoria creada por el usuario")
        @DeleteMapping("/{id}")
        public Mono<ResponseEntity<Void>> deleteUserCategory(@PathVariable UUID id) {
                return deleteMyCategoryUseCase.deleteMyCategory(id).thenReturn(ResponseEntity.noContent().build());
        }

}
