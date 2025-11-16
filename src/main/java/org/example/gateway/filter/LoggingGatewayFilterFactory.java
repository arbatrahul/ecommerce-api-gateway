package org.example.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Logging Gateway Filter
 * Logs all requests and responses for monitoring and debugging
 */
@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);
    
    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            
            String method = exchange.getRequest().getMethod().name();
            String path = exchange.getRequest().getPath().value();
            String clientIp = exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
            
            logger.info("🌐 Gateway Request: {} {} from {} - Started", method, path, clientIp);
            
            return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null ? 
                        exchange.getResponse().getStatusCode().value() : 0;
                    
                    logger.info("✅ Gateway Response: {} {} - Status: {} - Duration: {}ms", 
                        method, path, statusCode, duration);
                })
                .doOnError(throwable -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logger.error("❌ Gateway Error: {} {} - Duration: {}ms - Error: {}", 
                        method, path, duration, throwable.getMessage());
                });
        };
    }

    public static class Config {
        private boolean enabled = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
