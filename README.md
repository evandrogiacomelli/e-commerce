# E-Commerce API

A comprehensive RESTful e-commerce API built with Spring Boot, featuring customer and product management with complete CRUD operations.

## 📋 Features

### Customer Management
- ✅ Create, read, update, and delete customers
- ✅ Customer activation/deactivation
- ✅ Comprehensive customer data validation
- ✅ Customer address and document management
- ✅ Active customer filtering

### Product Management  
- ✅ Create, read, update, and delete products
- ✅ Product activation/deactivation
- ✅ Price validation and management
- ✅ Active product filtering

## 🏗️ Architecture

The project follows **Clean Architecture** principles with clear separation of concerns:

```
src/
├── main/java/com/evandro/e_commerce/
│   ├── customer/
│   │   ├── controller/     # REST endpoints
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── model/          # Domain entities
│   │   ├── dto/            # Data transfer objects
│   │   ├── factory/        # Object creation
│   │   └── exception/      # Custom exceptions
│   └── product/
│       ├── controller/     # REST endpoints
│       ├── service/        # Business logic
│       ├── repository/     # Data access
│       ├── model/          # Domain entities
│       ├── dto/            # Data transfer objects
│       ├── factory/        # Object creation
│       └── exception/      # Custom exceptions
└── test/                   # Comprehensive test suite
```

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Web** - RESTful API
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Maven** - Build tool

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd e-commerce
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **The API will be available at:** `http://localhost:8080`

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with clean build
./mvnw clean test
```

**Test Coverage:** 79 tests with 100% success rate ✅

## 📚 API Documentation

### Customer Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/customers` | Create a new customer |
| `GET` | `/customers/{id}` | Get customer by ID |
| `GET` | `/customers` | Get all customers |
| `GET` | `/customers/active` | Get active customers |
| `PUT` | `/customers/{id}` | Update customer |
| `PATCH` | `/customers/{id}/activate` | Activate customer |
| `PATCH` | `/customers/{id}/deactivate` | Deactivate customer |

### Product Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/products` | Create a new product |
| `GET` | `/products/{id}` | Get product by ID |
| `GET` | `/products` | Get all products |
| `GET` | `/products/active` | Get active products |
| `PUT` | `/products/{id}` | Update product |
| `PATCH` | `/products/{id}/activate` | Activate product |
| `PATCH` | `/products/{id}/deactivate` | Deactivate product |

### Example Requests

#### Create Customer
```http
POST /customers
Content-Type: application/json

{
  "name": "Evandro Giacomelli",
  "birthDate": "1994-10-05",
  "cpf": "055.988.200-77",
  "rg": "10.444.234-2",
  "zipCode": "83200-200",
  "street": "Rua dos Canários",
  "number": 44
}
```

#### Create Product
```http
POST /products
Content-Type: application/json

{
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop with RTX graphics",
  "price": 2999.99
}
```

## 🧪 Testing

The project includes comprehensive testing with:

- **Unit Tests**: Service layer, model validation, factories
- **Integration Tests**: Controller endpoints, repository operations
- **Test Coverage**: 79 tests covering all major functionality

### Test Categories
- ✅ **Controller Tests**: API endpoint validation
- ✅ **Service Tests**: Business logic verification  
- ✅ **Repository Tests**: Data access layer testing
- ✅ **Model Tests**: Entity validation
- ✅ **Factory Tests**: Object creation patterns

### Running Specific Tests
```bash
# Customer tests only
./mvnw test -Dtest="*Customer*"

# Product tests only  
./mvnw test -Dtest="*Product*"

# Controller tests only
./mvnw test -Dtest="*Controller*"
```

## 🗃️ Database

The application uses **H2 in-memory database** for development and testing:
- Automatically configured Spring Data JPA
- No external database setup required
- Perfect for development and testing environments

## ⚡ Key Features

### Data Validation
- **CPF/RG validation** for customers
- **Price validation** for products  
- **Address validation** with ZIP code
- **Required field validation**

### Exception Handling
- Custom exceptions for business logic errors
- Proper HTTP status code responses
- Clean error handling throughout the application

### Factory Pattern
- `CustomerFactory` for creating customer entities
- `ProductFactory` for creating product entities
- Centralized object creation logic

### Repository Pattern
- `InMemoryCustomerRepository` implementation
- `InMemoryProductRepository` implementation  
- Clean data access layer abstraction

## 🔄 Status Management

Both customers and products support status management:
- **ACTIVE**: Available for operations
- **INACTIVE**: Deactivated but preserved in system

## 📈 Project Status

- ✅ **Build Status**: Passing
- ✅ **Test Coverage**: 79/79 tests passing
- ✅ **Code Quality**: Clean architecture implementation
- ✅ **Documentation**: Comprehensive API documentation

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Developed with ❤️ using Spring Boot**