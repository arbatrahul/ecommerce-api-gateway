package org.example.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller for Circuit Breaker
 * Provides fallback responses when microservices are unavailable
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    @PostMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User Service Unavailable");
        response.put("message", "The user service is temporarily unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "user-service");
        response.put("status", "fallback");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/product-service")
    @PostMapping("/product-service")
    public ResponseEntity<Map<String, Object>> productServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Product Service Unavailable");
        response.put("message", "The product catalog is temporarily unavailable. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "product-service");
        response.put("status", "fallback");
        
        // Provide cached/default product data if available
        response.put("products", new Object[0]);
        response.put("totalItems", 0);
        response.put("currentPage", 0);
        response.put("totalPages", 0);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/cart-service")
    @PostMapping("/cart-service")
    public ResponseEntity<Map<String, Object>> cartServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Cart Service Unavailable");
        response.put("message", "The shopping cart service is temporarily unavailable. Your cart data is safe and will be restored when the service is back online.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "cart-service");
        response.put("status", "fallback");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/order-service")
    @PostMapping("/order-service")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Order Service Unavailable");
        response.put("message", "The order processing service is temporarily unavailable. Please try placing your order again in a few minutes.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "order-service");
        response.put("status", "fallback");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/payment-service")
    @PostMapping("/payment-service")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Payment Service Unavailable");
        response.put("message", "The payment processing service is temporarily unavailable. No charges have been made. Please try again later.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "payment-service");
        response.put("status", "fallback");
        response.put("chargeAttempted", false);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/notification-service")
    @PostMapping("/notification-service")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Notification Service Unavailable");
        response.put("message", "The notification service is temporarily unavailable. Notifications will be sent once the service is restored.");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "notification-service");
        response.put("status", "fallback");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "api-gateway");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "API Gateway is running and providing fallback responses");
        
        return ResponseEntity.ok(response);
    }
}
