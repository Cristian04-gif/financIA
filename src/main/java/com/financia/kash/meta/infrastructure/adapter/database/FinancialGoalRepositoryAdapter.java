package com.financia.kash.meta.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.meta.application.port.output.FinancialGoalRepositoryPort;
import com.financia.kash.meta.domain.model.FinancialGoal;
import com.financia.kash.meta.infrastructure.adapter.database.mapping.FinancialGoalMapper;
import com.financia.kash.meta.infrastructure.adapter.database.repository.FinancialGoalEntityRepository;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FinancialGoalRepositoryAdapter implements FinancialGoalRepositoryPort {

    private final FinancialGoalEntityRepository financialGoalEntityRepository;
    private final FinancialGoalMapper financialGoalMapper;
    private final EntityManager entityManager;

    @Override
    public FinancialGoal save(FinancialGoal financialGoal) {
        UserEntity user = entityManager.getReference(UserEntity.class, financialGoal.getUserId());
        return financialGoalMapper.mapToDomain(
                financialGoalEntityRepository.save(financialGoalMapper.mapToEntity(financialGoal, user)));
    }

    @Override
    public FinancialGoal findById(UUID id) {
        return financialGoalEntityRepository.findById(id)
                .map(financialGoalMapper::mapToDomain)
                .orElseThrow(() -> new IllegalArgumentException("Meta financiera no encontrada: " + id));
    }

    @Override
    public List<FinancialGoal> findByUserId(UUID userId) {
        return financialGoalEntityRepository.findAllByUser_Id(userId).stream()
                .map(financialGoalMapper::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        financialGoalEntityRepository.deleteById(id);
    }
}
