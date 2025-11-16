package org.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**", "/api/auth/**")
                        .uri("lb://user-service"))
                
                // Product Service Routes (Elasticsearch search)
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .uri("lb://product-service"))
                
                // Cart Service Routes
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .uri("lb://cart-service"))
                
                // Order Service Routes
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("lb://order-service"))
                
                // Payment Service Routes
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .uri("lb://payment-service"))
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("lb://notification-service"))
                
                .build();
    }
}
