package com.financia.kash.movimiento.categoria.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.application.port.output.UserForCategoryPort;
import com.financia.kash.movimiento.categoria.application.service.CategoryService;

@Configuration
public class CategoryBeanConfiguration {

    @Bean
    public CategoryService categoryService(CategoryRepositoryPort categoryRepositoryPort,
            UserForCategoryPort userForCategoryPort) {
        return new CategoryService(categoryRepositoryPort, userForCategoryPort);
    }
}
