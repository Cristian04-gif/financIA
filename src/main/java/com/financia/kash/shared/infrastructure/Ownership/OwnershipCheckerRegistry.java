package com.financia.kash.shared.infrastructure.Ownership;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class OwnershipCheckerRegistry {

    private final Map<ResourceType, OwnershipChecker> checkers;

    public OwnershipCheckerRegistry(List<OwnershipChecker> ownershipCheckers) {
        this.checkers = ownershipCheckers.stream()
                .collect(Collectors.toMap(OwnershipChecker::supports, Function.identity()));
    }

    public OwnershipChecker get(ResourceType resourceType) {
        OwnershipChecker checker = checkers.get(resourceType);

        if (checker == null) {
            throw new IllegalArgumentException("no existe OwnershipCheker para " + resourceType);
        }

        return checker;
    }
}
