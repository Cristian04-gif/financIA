package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.entity.SubscriptionEntity;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.repository.project.SubscriptionProject;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SubscriptionMapper {

    Subscription mapToDomain(SubscriptionEntity entity);

    SubscriptionEntity mapToEntity(Subscription subscription);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "payingAccountId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "payDay", ignore = true)
    Subscription mapToDomain(SubscriptionProject project);

    SubscriptionProject mapToProject(Subscription subscription);

}
