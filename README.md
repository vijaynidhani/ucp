# UCP - Universal Commerce Platform

A Spring Boot application demonstrating modern design patterns for payment processing with H2 database support.

## Features

### H2 Database Support
- In-memory H2 database for development and testing
- Spring Data JPA for persistence
- Automatic schema generation

### Payment Processing System

The payment processing system implements several design patterns:

#### Facade Pattern
The `PaymentFacade` provides a simplified interface for payment processing, hiding the complexity of:
- Payment gateway selection
- Charge calculation based on destination country
- Payment record persistence

#### Adapter Pattern
Payment gateways are implemented as adapters with a common `PaymentGateway` interface:
- **UPI Gateway**: For UPI-based payments
- **Card Gateway**: For card-based payments
- **Apple Pay Gateway**: For Apple Pay-based payments

All gateways have access to ChargeStrategy for potential gateway-specific charge calculations.

#### Strategy Pattern
The `ChargeStrategy` interface allows flexible charge calculation:
- **CountryBasedChargeStrategy**: Calculates charges based on destination country

## API Endpoints

### Payment APIs

#### Process Payment
```bash
POST /api/payments/process
Content-Type: application/json

{
  "name": "John Doe",
  "toAccount": "9876543210",
  "fromAccount": "1234567890",
  "description": "Payment for services",
  "destinationCountry": "IN",  // Optional, defaults to "IN" if not provided
  "paymentMethod": "UPI",       // "UPI", "CARD", or "APPLE_PAY"
  "amount": 1000.0
}
```

**Response:**
```json
{
  "paymentId": 1,
  "status": "SUCCESS",
  "message": "Payment processed successfully via UPI",
  "totalAmount": 1010.0,
  "charges": 10.0,
  "gatewayUsed": "UPI"
}
```

#### Get Available Gateways
```bash
GET /api/payments/gateways
```

**Response:**
```json
["APPLE_PAY", "CARD", "UPI"]
```

### Other APIs

#### Hello Endpoint
```bash
GET /hello
```

**Response:**
```
Hello, World!
```

## Charge Rates by Country

The system applies different charges based on the destination country:

| Country | Code | Charge Rate |
|---------|------|-------------|
| India | IN | 1.0% |
| USA | US | 3.0% |
| UK | GB | 2.5% |
| Europe | EU | 2.8% |
| Others | - | 3.5% |

## Technology Stack

- **Java**: 17
- **Spring Boot**: 4.1.0-M1
- **Database**: H2 (in-memory)
- **ORM**: Hibernate/JPA
- **Logging**: SLF4J with Logback
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Building the Project
```bash
mvn clean install
```

### Running the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Running Tests
```bash
mvn test
```

## Database

The application uses H2 in-memory database with the following configuration:
- **URL**: `jdbc:h2:mem:ucpdb`
- **Username**: `sa`
- **Password**: (empty)
- **Console**: Enabled at `/h2-console` (for web-servlet environments)

## Configuration

The application supports the following configuration properties in `application.properties`:

### Payment Configuration
- **payment.default.country**: Default destination country for payments (default: "IN")
  - Used when no destination country is specified in payment request
  - Example: `payment.default.country=IN`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/altruist/projects/ucp/
│   │       ├── payment/
│   │       │   ├── controller/      # REST controllers
│   │       │   ├── dto/             # Data transfer objects
│   │       │   ├── gateway/         # Payment gateway adapters
│   │       │   ├── model/           # JPA entities
│   │       │   ├── repository/      # Spring Data repositories
│   │       │   ├── service/         # Business logic (Facade)
│   │       │   └── strategy/        # Charge calculation strategies
│   │       ├── HelloController.java
│   │       └── UcpApplication.java
│   └── resources/
│       ├── application.properties
│       └── logback-spring.xml
└── test/
    └── java/
        └── com/altruist/projects/ucp/
            ├── payment/
            │   ├── service/         # Facade tests
            │   └── strategy/        # Strategy tests
            └── UcpApplicationTests.java
```

## Design Patterns Used

### 1. Facade Pattern
**Location**: `PaymentFacade.java`

Provides a unified interface to a set of interfaces in the payment subsystem, making it easier to use.

### 2. Adapter Pattern
**Location**: `UpiPaymentGateway.java`, `CardPaymentGateway.java`

Allows incompatible payment gateway interfaces to work together through a common `PaymentGateway` interface.

### 3. Strategy Pattern
**Location**: `ChargeStrategy.java`, `CountryBasedChargeStrategy.java`

Defines a family of algorithms for charge calculation, encapsulates each one, and makes them interchangeable.

## Testing

The project includes comprehensive tests:
- Unit tests for PaymentFacade (including Apple Pay and default country tests)
- Unit tests for ChargeStrategy
- Integration tests for the application context

**Test Coverage**: 14 tests, 0 failures

## Logging

The application uses SLF4J with Logback for logging:
- Console output with timestamp and thread information
- File output to `logs/ucp-application.log`
- Configurable log levels per package

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is a demonstration/learning project.

## Security Summary

✅ No security vulnerabilities detected by CodeQL analysis.
