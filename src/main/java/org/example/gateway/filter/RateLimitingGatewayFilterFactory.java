package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate Limiting Gateway Filter
 * Implements request rate limiting to prevent abuse and ensure fair usage
 */
@Component
public class RateLimitingGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimitingGatewayFilterFactory.Config> {

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    public RateLimitingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = getClientId(exchange);
            
            if (isRateLimited(clientId, config)) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getRequestsPerMinute()));
                response.getHeaders().add("X-RateLimit-Remaining", "0");
                response.getHeaders().add("Retry-After", "60");
                
                String body = "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Please try again later.\"}";
                DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                return response.writeWith(Mono.just(buffer));
            }
            
            return chain.filter(exchange);
        };
    }

    private String getClientId(org.springframework.web.server.ServerWebExchange exchange) {
        // Use IP address as client identifier
        String clientIp = exchange.getRequest().getRemoteAddress() != null ? 
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
        
        // You could also use JWT token or API key for more sophisticated identification
        return clientIp;
    }

    private boolean isRateLimited(String clientId, Config config) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (60 * 1000); // 1 minute window
        
        RateLimitInfo rateLimitInfo = rateLimitMap.computeIfAbsent(clientId, k -> new RateLimitInfo());
        
        // Clean old requests outside the window
        if (rateLimitInfo.getWindowStart() < windowStart) {
            rateLimitInfo.reset(currentTime);
        }
        
        // Check if rate limit exceeded
        if (rateLimitInfo.getRequestCount() >= config.getRequestsPerMinute()) {
            return true;
        }
        
        // Increment request count
        rateLimitInfo.incrementRequests();
        return false;
    }

    public static class Config {
        private int requestsPerMinute = 100; // Default: 100 requests per minute
        
        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }
        
        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }

    private static class RateLimitInfo {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        
        public int getRequestCount() {
            return requestCount.get();
        }
        
        public long getWindowStart() {
            return windowStart.get();
        }
        
        public void incrementRequests() {
            requestCount.incrementAndGet();
        }
        
        public void reset(long newWindowStart) {
            requestCount.set(0);
            windowStart.set(newWindowStart);
        }
    }
}
