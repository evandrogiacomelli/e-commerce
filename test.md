# E-Commerce API Test Guide - Postman Collection

## Application Setup

- **Base URL**: `http://localhost:8080`
- **Start Application**: `mvn spring-boot:run`
- **H2 Console**: `http://localhost:8080/h2-console`

## Test Data Available

### Products (8 items)
- Gaming Laptop: `a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d`
- Wireless Mouse: `b2c3d4e5-f6a7-4b8c-9d1e-2f3a4b5c6d7e`
- Mechanical Keyboard: `c3d4e5f6-a7b8-4c9d-1e2f-3a4b5c6d7e8f`
- Gaming Monitor: `d4e5f6a7-b8c9-4d1e-2f3a-4b5c6d7e8f9a`
- Gaming Headset: `e5f6a7b8-c9d1-4e2f-3a4b-5c6d7e8f9a0b`
- SSD Drive: `f6a7b8c9-d1e2-4f3a-4b5c-6d7e8f9a0b1c`
- Webcam (inactive): `a7b8c9d1-e2f3-4a4b-5c6d-7e8f9a0b1c2d`
- Graphics Card: `b8c9d1e2-f3a4-4b5c-6d7e-8f9a0b1c2d3e`

### Customers (5 users)
- João Silva: `a1b2c3d4-e5f6-4a7b-8c9d-000000000001`
- Maria Santos: `b2c3d4e5-f6a7-4b8c-9d1e-000000000002`
- Pedro Oliveira: `c3d4e5f6-a7b8-4c9d-1e2f-000000000003`
- Ana Costa: `d4e5f6a7-b8c9-4d1e-2f3a-000000000004`
- Carlos Ferreira (inactive): `e5f6a7b8-c9d1-4e2f-3a4b-000000000005`

## 1. PRODUCTS API

### GET - All Products
- **URL**: `GET http://localhost:8080/products`

### GET - Active Products Only
- **URL**: `GET http://localhost:8080/products/active`

### GET - Product by ID
- **URL**: `GET http://localhost:8080/products/a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d`

### POST - Create Product
- **URL**: `POST http://localhost:8080/products`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "name": "New Product",
  "description": "Product description",
  "price": 199.99
}
```

### PUT - Update Product
- **URL**: `PUT http://localhost:8080/products/a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d`
- **Body**:
```json
{
  "name": "Gaming Laptop Pro",
  "description": "Updated description",
  "price": 2699.99
}
```

### PATCH - Deactivate Product
- **URL**: `PATCH http://localhost:8080/products/a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d/deactivate`

### PATCH - Activate Product
- **URL**: `PATCH http://localhost:8080/products/a7b8c9d1-e2f3-4a4b-5c6d-7e8f9a0b1c2d/activate`

## 2. CUSTOMERS API

### GET - All Customers
- **URL**: `GET http://localhost:8080/customers`

### GET - Active Customers Only
- **URL**: `GET http://localhost:8080/customers/active`

### GET - Customer by ID
- **URL**: `GET http://localhost:8080/customers/a1b2c3d4-e5f6-4a7b-8c9d-000000000001`

### POST - Create Customer
- **URL**: `POST http://localhost:8080/customers`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "name": "New Customer",
  "birthDate": "1995-01-01",
  "cpf": "111.222.333-44",
  "rg": "11.222.333-4",
  "zipCode": "12345-678",
  "street": "New Street",
  "number": 999
}
```

### PUT - Update Customer
- **URL**: `PUT http://localhost:8080/customers/a1b2c3d4-e5f6-4a7b-8c9d-000000000001`
- **Body**:
```json
{
  "name": "João Silva Updated",
  "birthDate": "1990-05-15",
  "cpf": "123.456.789-01",
  "rg": "12.345.678-9",
  "zipCode": "01234-567",
  "street": "Rua das Flores Updated",
  "number": 1234
}
```

### PATCH - Deactivate Customer
- **URL**: `PATCH http://localhost:8080/customers/a1b2c3d4-e5f6-4a7b-8c9d-000000000001/deactivate`

### PATCH - Activate Customer
- **URL**: `PATCH http://localhost:8080/customers/e5f6a7b8-c9d1-4e2f-3a4b-000000000005/activate`

## 3. ORDERS API

### GET - All Orders
- **URL**: `GET http://localhost:8080/orders`

### POST - Create Order
- **URL**: `POST http://localhost:8080/orders`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "customerId": "a1b2c3d4-e5f6-4a7b-8c9d-000000000001"
}
```

### GET - Order by ID
- **URL**: `GET http://localhost:8080/orders/{{orderId}}`

### GET - Orders by Customer
- **URL**: `GET http://localhost:8080/orders/customer/a1b2c3d4-e5f6-4a7b-8c9d-000000000001`

### POST - Add Item to Order
- **URL**: `POST http://localhost:8080/orders/{{orderId}}/items`
- **Body**:
```json
{
  "productId": "a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d",
  "quantity": 1,
  "salePrice": 2499.99
}
```

### POST - Add Another Item
- **URL**: `POST http://localhost:8080/orders/{{orderId}}/items`
- **Body**:
```json
{
  "productId": "b2c3d4e5-f6a7-4b8c-9d1e-2f3a4b5c6d7e",
  "quantity": 2,
  "salePrice": 79.99
}
```

### PUT - Update Item Quantity
- **URL**: `PUT http://localhost:8080/orders/{{orderId}}/items/a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d`
- **Body**:
```json
{
  "quantity": 3
}
```

### DELETE - Remove Item from Order
- **URL**: `DELETE http://localhost:8080/orders/{{orderId}}/items/b2c3d4e5-f6a7-4b8c-9d1e-2f3a4b5c6d7e`

## 4. ORDER WORKFLOW

### PATCH - Finalize Order
- **URL**: `PATCH http://localhost:8080/orders/{{orderId}}/finalize`

### PATCH - Process Payment
- **URL**: `PATCH http://localhost:8080/orders/{{orderId}}/pay`

### PATCH - Deliver Order
- **URL**: `PATCH http://localhost:8080/orders/{{orderId}}/deliver`

### PATCH - Cancel Order
- **URL**: `PATCH http://localhost:8080/orders/{{orderId}}/cancel`

## Test Scenarios

### Scenario 1: Complete E-commerce Flow
1. GET all products and customers (verify test data loaded)
2. Create order for João Silva: `a1b2c3d4-e5f6-4a7b-8c9d-000000000001`
3. Add Gaming Laptop: `a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d`
4. Add Wireless Mouse: `b2c3d4e5-f6a7-4b8c-9d1e-2f3a4b5c6d7e`
5. Finalize → Pay → Deliver

### Scenario 2: Product Management
1. Try to activate already active product
2. Deactivate Gaming Monitor: `d4e5f6a7-b8c9-4d1e-2f3a-4b5c6d7e8f9a`
3. Activate Webcam: `a7b8c9d1-e2f3-4a4b-5c6d-7e8f9a0b1c2d`

### Scenario 3: Customer Management
1. Activate Carlos Ferreira: `e5f6a7b8-c9d1-4e2f-3a4b-000000000005`
2. Create order for him
3. Deactivate Maria Santos: `b2c3d4e5-f6a7-4b8c-9d1e-000000000002`

### Scenario 4: Order Modifications
1. Create order with 3 items
2. Remove middle item
3. Update quantities
4. Cancel order

## H2 Database Access

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)

### Quick SQL Queries
```sql
SELECT * FROM products;
SELECT * FROM customers;
SELECT * FROM products WHERE status = 'ACTIVE';
SELECT * FROM customers WHERE status = 0;
```

## Response Codes
- **200**: Success
- **201**: Created
- **400**: Bad Request
- **404**: Not Found

## Order States
`CREATED → FINALIZED → PAID → DELIVERED`
`CANCELLED` (from any state except DELIVERED)