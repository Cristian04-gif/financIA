package com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto;

import java.util.List;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

public record CategoryResponse(List<ProjectCategory> glogeneralCategories, List<ProjectCategory> userCategories) {

}
