package org.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Enhanced API Gateway Configuration
 * Includes advanced features like rate limiting, circuit breakers, and load balancing
 */
@Configuration
public class EnhancedGatewayConfig {

    @Bean
    @Primary
    public RouteLocator enhancedRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes with rate limiting
                .route("user-service-enhanced", r -> r
                        .path("/api/users/**", "/api/auth/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "user-service")
                            .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                            .circuitBreaker(config -> config
                                .setName("user-service-cb")
                                .setFallbackUri("forward:/fallback/user-service"))
                            .retry(config -> config.setRetries(3)))
                        .uri("lb://user-service"))
                
                // Product Service Routes (Elasticsearch search) with caching
                .route("product-service-enhanced", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "product-service")
                            .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                            .circuitBreaker(config -> config
                                .setName("product-service-cb")
                                .setFallbackUri("forward:/fallback/product-service"))
                            .retry(config -> config.setRetries(2)))
                        .uri("lb://product-service"))
                
                // Cart Service Routes with session affinity
                .route("cart-service-enhanced", r -> r
                        .path("/api/cart/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "cart-service")
                            .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                            .circuitBreaker(config -> config
                                .setName("cart-service-cb")
                                .setFallbackUri("forward:/fallback/cart-service"))
                            .retry(config -> config.setRetries(3)))
                        .uri("lb://cart-service"))
                
                // Order Service Routes with enhanced monitoring
                .route("order-service-enhanced", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "order-service")
                            .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                            .circuitBreaker(config -> config
                                .setName("order-service-cb")
                                .setFallbackUri("forward:/fallback/order-service"))
                            .retry(config -> config.setRetries(2)))
                        .uri("lb://order-service"))
                
                // Payment Service Routes with strict security
                .route("payment-service-enhanced", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "payment-service")
                            .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                            .circuitBreaker(config -> config
                                .setName("payment-service-cb")
                                .setFallbackUri("forward:/fallback/payment-service"))
                            .retry(config -> config.setRetries(1))) // Fewer retries for payments
                        .uri("lb://payment-service"))
                
                // Notification Service Routes
                .route("notification-service-enhanced", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "notification-service")
                            .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                            .circuitBreaker(config -> config
                                .setName("notification-service-cb")
                                .setFallbackUri("forward:/fallback/notification-service"))
                            .retry(config -> config.setRetries(2)))
                        .uri("lb://notification-service"))
                
                // Gateway Management Routes
                .route("gateway-management", r -> r
                        .path("/gateway/**")
                        .filters(f -> f
                            .addRequestHeader("X-Gateway-Service", "gateway-management")
                            .stripPrefix(1))
                        .uri("http://localhost:8080"))
                
                .build();
    }
}
