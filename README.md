# Ecommerce API Gateway

Central API Gateway service for routing requests to microservices in the Ecommerce Platform.

## Overview

The API Gateway is the single entry point for all client requests to the microservices architecture. It provides:
- **Request Routing**: Routes requests to appropriate microservices
- **Load Balancing**: Distributes traffic across service instances
- **Circuit Breaker**: Handles service failures gracefully
- **JWT Authentication**: Validates JWT tokens for secured endpoints
- **CORS Configuration**: Manages cross-origin resource sharing
- **Fallback Responses**: Provides fallback when services are unavailable

## Features

- ✅ **Spring Cloud Gateway**: Reactive, non-blocking gateway
- ✅ **Service Discovery**: Eureka client integration
- ✅ **Load Balancing**: Spring Cloud LoadBalancer
- ✅ **Circuit Breaker**: Resilience4j integration
- ✅ **JWT Security**: Token validation and filtering
- ✅ **Global CORS**: Cross-origin support
- ✅ **Fallback Controller**: Graceful degradation
- ✅ **Request Logging**: Custom logging filters
- ✅ **Rate Limiting**: Request rate limiting (optional)

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Eureka Server running on port 8761
- All microservices registered with Eureka

### Running Locally

1. **Build the project**:
   ```bash
   mvn clean package
   ```

2. **Run the application**:
   ```bash
   java -jar target/api-gateway-1.0.0.jar
   ```

3. **Or use Maven**:
   ```bash
   mvn spring-boot:run
   ```

The gateway will start on `http://localhost:8080`

### Service Endpoints

Once running, the gateway routes requests to microservices:

#### User Service Routes
- `GET/POST /api/users/**` → `user-service`
- `GET/POST /api/auth/**` → `user-service`

#### Product Service Routes
- `GET/POST /api/products/**` → `product-service`

#### Cart Service Routes
- `GET/POST /api/cart/**` → `cart-service`

#### Order Service Routes
- `GET/POST /api/orders/**` → `order-service`

#### Payment Service Routes
- `GET/POST /api/payments/**` → `payment-service`

#### Notification Service Routes
- `GET/POST /api/notifications/**` → `notification-service`

#### Fallback Endpoints
- `GET/POST /fallback/user-service` - User service fallback
- `GET/POST /fallback/product-service` - Product service fallback
- `GET/POST /fallback/cart-service` - Cart service fallback
- `GET/POST /fallback/order-service` - Order service fallback
- `GET/POST /fallback/payment-service` - Payment service fallback
- `GET/POST /fallback/notification-service` - Notification service fallback
- `GET /fallback/health` - Gateway health check

## Configuration

### Application Configuration (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
```

### Circuit Breaker Configuration

```yaml
spring:
  cloud:
    circuitbreaker:
      resilience4j:
        enabled: true
        configs:
          default:
            slidingWindowSize: 10
            minimumNumberOfCalls: 5
            failureRateThreshold: 50
            waitDurationInOpenState: 30s
```

### Eureka Configuration

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

## Architecture

### Request Flow

1. **Client Request** → API Gateway (Port 8080)
2. **Route Matching** → Gateway matches path to service route
3. **JWT Validation** → Validates token for secured endpoints
4. **Load Balancing** → Selects service instance via Eureka
5. **Circuit Breaker** → Checks service availability
6. **Service Call** → Forwards request to microservice
7. **Response** → Returns service response or fallback

### Components

1. **GatewayConfig**: Defines route configurations
2. **EnhancedGatewayConfig**: Advanced routing and filtering
3. **LoadBalancerConfig**: Load balancing configuration
4. **FallbackController**: Handles service unavailability
5. **LoggingGatewayFilterFactory**: Request/response logging
6. **RateLimitingGatewayFilterFactory**: Rate limiting (optional)

## API Usage Examples

### Accessing Services Through Gateway

All microservice endpoints are accessible through the gateway:

```bash
# User Service - Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'

# Product Service - Search Products
curl "http://localhost:8080/api/products/search?keyword=laptop&page=0&size=12"

# Cart Service - Get Cart
curl http://localhost:8080/api/cart/1

# Order Service - Create Order
curl -X POST http://localhost:8080/api/orders/checkout/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "shippingAddress": {...},
    "paymentMethod": "CREDIT_CARD"
  }'
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Gateway Health

```bash
curl http://localhost:8080/fallback/health
```

## Testing

### Run Tests

```bash
mvn test
```

### Manual Testing

1. Start Eureka Server
2. Start all microservices
3. Start API Gateway
4. Test routes through gateway
5. Test fallback by stopping a service

## Deployment

### Docker

```bash
# Build image
docker build -t ecommerce/api-gateway .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/ \
  ecommerce/api-gateway
```

### Production Considerations

1. **Service Discovery**: Ensure Eureka server is highly available
2. **Load Balancing**: Configure multiple service instances
3. **Circuit Breaker**: Tune thresholds for production load
4. **Security**: 
   - Use strong JWT secrets
   - Enable HTTPS
   - Configure proper CORS
5. **Monitoring**: Enable metrics and logging
6. **Scaling**: Deploy multiple gateway instances behind load balancer

## Troubleshooting

### Common Issues

1. **Services Not Found**:
   - Verify Eureka server is running
   - Check service registration in Eureka dashboard
   - Verify service names match route configuration

2. **Connection Refused**:
   - Check if target service is running
   - Verify service port configuration
   - Check network connectivity

3. **Circuit Breaker Open**:
   - Service may be down or slow
   - Check service health endpoints
   - Review circuit breaker logs

4. **JWT Validation Fails**:
   - Verify JWT secret matches user-service
   - Check token expiration
   - Verify token format

### Logs

Check application logs for detailed error information:

```bash
# Enable debug logging
java -jar target/api-gateway-1.0.0.jar \
  --logging.level.org.springframework.cloud.gateway=DEBUG
```

## Dependencies

- Spring Boot 3.2.0
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Cloud LoadBalancer
- Resilience4j Circuit Breaker
- Spring Security (JWT validation)
- JWT (io.jsonwebtoken)

## Project Structure

```
src/
├── main/
│   ├── java/org/example/gateway/
│   │   ├── ApiGatewayApplication.java
│   │   ├── config/
│   │   │   ├── GatewayConfig.java          # Route definitions
│   │   │   ├── EnhancedGatewayConfig.java   # Advanced routing
│   │   │   └── LoadBalancerConfig.java      # Load balancer config
│   │   ├── controller/
│   │   │   └── FallbackController.java      # Fallback responses
│   │   └── filter/
│   │       ├── LoggingGatewayFilterFactory.java
│   │       └── RateLimitingGatewayFilterFactory.java
│   └── resources/
│       └── application.yml
└── test/
```

## Contributing

1. Follow Spring Cloud Gateway best practices
2. Test all routes thoroughly
3. Update route documentation
4. Handle errors gracefully
5. Use proper logging levels

## License

This project is part of the Ecommerce Microservices Platform.
