package com.financia.kash.shared.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import io.lettuce.core.resource.ClientResources;
import io.netty.resolver.DefaultAddressResolverGroup;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return ClientResources.builder()
                .addressResolverGroup(DefaultAddressResolverGroup.INSTANCE)
                .build();
    }

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(ClientResources clientResources) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host.trim(), port);
        redisConfig.setPassword(password.trim());

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientResources(clientResources)
                .useSsl()
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }
}
