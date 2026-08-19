package com.financia.kash.categoria.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.categoria.application.service.CategoryService;

@Configuration
public class CategoryBeanConfiguration {

    @Bean
    public CategoryService categoryService(CategoryRepositoryPort categoryRepositoryPort) {
        return new CategoryService(categoryRepositoryPort);
    }
}
