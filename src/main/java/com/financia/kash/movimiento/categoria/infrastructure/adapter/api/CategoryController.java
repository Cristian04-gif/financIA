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

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
public class CategoryController {

        private final GetCategoriesUseCase getCategoriesUseCase;
        private final CreateCategoryUseCase createCategoryUseCase;
        private final UpdateCategoryUseCase updateCategoryUseCase;
        private final DeleteMyCategoryUseCase deleteMyCategoryUseCase;
        private final CategoryMapper categoryMapper;

        @Operation(summary = "Categorias globales", description = "Devuelve las categorias globales")
        @GetMapping("/global")
        public ResponseEntity<List<CategoryResponse>> getGlobalCategories() {
                List<CategoryResponse> globalCategories = getCategoriesUseCase.getGlobalCategories().stream()
                                .map(categoryMapper::mapToResponse).toList();
                return ResponseEntity.ok(globalCategories);
        }

        @Operation(summary = "Categorias del usuario", description = "Devuelve las categorias creadas por el usuario logeado")
        @GetMapping("/of-user")
        public ResponseEntity<List<CategoryResponse>> getGlobalCategories(@AuthenticationPrincipal UserEntity user) {
                UUID userId = user.getId();
                List<CategoryResponse> userCategories = getCategoriesUseCase.getAllMyCategory(userId).stream()
                                .map(categoryMapper::mapToResponse).toList();
                return ResponseEntity.ok(userCategories);
        }

        @PreAuthorize("""
                            hasAuthority('ADMIN') or
                            (hasAuthority('USER') and
                             @securityService.isOwner(#id, authentication.name, T(com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity)))
                        """)
        @Operation(summary = "Categoria", description = "Devuelve la informacion por su ID")
        @GetMapping("/{id}")
        public ResponseEntity<Category> getById(@PathVariable UUID id) {
                Category category = getCategoriesUseCase.getById(id);
                return ResponseEntity.ok(category);
        }

        @PreAuthorize("hasAuthority('USER')")
        @Operation(summary = "Sub-categoria", description = "Crea una categoria que necesite usuario")
        @PostMapping("/of-user")
        public ResponseEntity<Category> createUserCategory(@AuthenticationPrincipal UserEntity user,
                        @RequestBody CategoryForUserRequest request) {
                Category category = createCategoryUseCase.createCategoryForUser(user.getId(), request.name(),
                                request.type(),
                                request.parentCategoryId());
                return ResponseEntity.status(HttpStatus.CREATED).body(category);
        }

        @PreAuthorize("hasAuthority('ADMIN')")
        @Operation(summary = "Categoria global", description = "Crea una categoria globalizada")
        @PostMapping("/global")
        public ResponseEntity<Category> createGlobalCategory(@RequestBody CategoryGlobalRequest request) {
                Category category = createCategoryUseCase.createMainCategory(request.name(), request.type());
                return ResponseEntity.status(HttpStatus.CREATED).body(category);
        }

        @PreAuthorize("""
                        hasAuthority('USER') and @securityService.isOwner(#id, authentication.name, T(com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity))
                        """)
        @Operation(summary = "Actualizar categoria", description = "Actualiza una categoria del usuario")
        @PutMapping("/of-user/{id}")
        public ResponseEntity<Category> updateUsercategory(@PathVariable UUID id,
                        @AuthenticationPrincipal UserDetails user, @RequestBody CategoryForUserRequest request) {

                Category category = updateCategoryUseCase.updateCategoryForUser(id, request.name(), request.type(),
                                request.parentCategoryId(),
                                request.active());

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(category);
        }

        @PreAuthorize("""
                        hasAuthority('USER') and @securityService.isOwner(#id, authentication.name, T(com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity))
                        """)
        @Operation(summary = "Eliminar categoria", description = "Elimina una categoria creada por el usuario")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUserCategory(@PathVariable UUID id,
                        @AuthenticationPrincipal UserEntity user) {
                deleteMyCategoryUseCase.deleteMyCategory(id);
                return ResponseEntity.noContent().build();
        }

}
