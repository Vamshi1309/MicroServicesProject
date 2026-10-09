package com.ragplatform.api_gateway.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Value("${jwt.secret}")
        private String secret;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .authorizeExchange(exchange -> exchange
                                                .pathMatchers("/auth/**").permitAll()
                                                .pathMatchers("/actuator/health").permitAll()
                                                .pathMatchers("/actuator/gateway/routes").permitAll()
                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                                }))
                                .build();
        }

        @Bean
        public ReactiveJwtDecoder jwtDecoder() {

                SecretKeySpec key = new SecretKeySpec(
                                secret.getBytes(),
                                "HmacSHA256");

                return NimbusReactiveJwtDecoder
                                .withSecretKey(key)
                                .build();
        }
}
