# Postman API Testing Guide

## Overview
This guide provides instructions for testing the microservices architecture using the provided Postman collection.

## Prerequisites
1. Docker Desktop running
2. All microservices running via `docker-compose up`
3. Postman installed on your machine

## Import the Collection

### Method 1: Import from File
1. Open Postman
2. Click **Import** button (top-left corner)
3. Select **File** tab
4. Choose `Microservices_API_Collection.postman_collection.json`
5. Click **Import**

### Method 2: Drag & Drop
1. Open Postman
2. Drag the `Microservices_API_Collection.postman_collection.json` file into Postman window

## Collection Structure

The collection contains 4 main folders:

### 1. Item Service (3 endpoints)
- **GET /items** - Retrieve all items
- **POST /items** - Add a new item
- **GET /items/{id}** - Get item by ID

### 2. Order Service (3 endpoints)
- **GET /orders** - Retrieve all orders
- **POST /orders** - Place a new order
- **GET /orders/{id}** - Get order by ID

### 3. Payment Service (3 endpoints)
- **GET /payments** - Retrieve all payments
- **POST /payments/process** - Process a payment
- **GET /payments/{id}** - Get payment by ID

### 4. Complete Workflow Test (6 requests)
An automated test sequence that:
1. Adds an item
2. Places an order
3. Processes payment
4. Verifies item creation
5. Verifies order creation
6. Verifies payment processing

## How to Use

### Testing Individual Endpoints

#### 1. Add an Item
```http
POST http://localhost:8080/items
Content-Type: application/json

{
    "name": "Laptop",
    "price": 999.99,
    "description": "High-performance laptop",
    "quantity": 10
}
```

#### 2. Place an Order
```http
POST http://localhost:8080/orders
Content-Type: application/json

{
    "itemId": 1,
    "quantity": 2,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "totalAmount": 1999.98
}
```

#### 3. Process Payment
```http
POST http://localhost:8080/payments/process
Content-Type: application/json

{
    "orderId": 1,
    "amount": 1999.98,
    "paymentMethod": "Credit Card",
    "cardNumber": "**** **** **** 1234",
    "customerName": "John Doe"
}
```

### Running the Complete Workflow Test

The **Complete Workflow Test** folder includes automated tests with assertions:

1. **Select the folder** "Complete Workflow Test"
2. Click **Run** button (top-right)
3. In the Collection Runner window, click **Run Microservices API Collection**
4. Watch all 6 requests execute in sequence
5. Review test results and assertions

**Features:**
- Automatically captures IDs from responses
- Uses environment variables to link requests
- Validates status codes (200, 201)
- Verifies response data structure
- Checks business logic (order status = "PENDING", payment status = "SUCCESS")

## Expected Responses

### Successful Item Creation (201)
```json
{
    "id": 1,
    "name": "Laptop",
    "price": 999.99,
    "description": "High-performance laptop",
    "quantity": 10
}
```

### Successful Order Creation (201)
```json
{
    "id": 1,
    "itemId": 1,
    "quantity": 2,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "totalAmount": 1999.98,
    "status": "PENDING"
}
```

### Successful Payment Processing (201)
```json
{
    "id": 1,
    "orderId": 1,
    "amount": 1999.98,
    "paymentMethod": "Credit Card",
    "cardNumber": "**** **** **** 1234",
    "customerName": "John Doe",
    "status": "SUCCESS"
}
```

### Not Found (404)
When requesting a non-existent resource:
```
Status: 404 Not Found
```

## Service Ports

All requests go through the **API Gateway** at:
```
http://localhost:8080
```

Individual services (for direct access if needed):
- **Item Service**: http://localhost:8081
- **Order Service**: http://localhost:8082
- **Payment Service**: http://localhost:8083
- **API Gateway**: http://localhost:8080

## Troubleshooting

### Connection Refused Error
**Problem:** Cannot connect to localhost:8080

**Solution:**
1. Verify Docker containers are running: `docker ps`
2. Check if api-gateway container is healthy
3. Review logs: `docker-compose logs api-gateway`

### 404 Not Found on Valid Endpoints
**Problem:** Getting 404 on /items, /orders, or /payments

**Solution:**
1. Verify all services are running: `docker-compose ps`
2. Check API Gateway routing configuration
3. Restart services: `docker-compose restart`

### Empty Response Arrays
**Problem:** GET requests return empty arrays []

**Solution:**
- This is normal! Services start with no data
- Use POST requests to add data first
- Then use GET requests to retrieve data

## Tips for Testing

1. **Start with Individual Requests**: Test each service separately before running the workflow
2. **Check Status Codes**: 
   - 200 = Success (GET)
   - 201 = Created (POST)
   - 404 = Not Found
3. **Use Collection Runner**: Run the entire workflow automatically for regression testing
4. **Environment Variables**: The workflow test uses environment variables (itemId, orderId, paymentId) to chain requests
5. **Modify Request Bodies**: Feel free to change the sample data in POST requests

## Sample Test Sequence

```
1. POST /items           → Create item (get itemId = 1)
2. GET /items            → Verify item list contains 1 item
3. GET /items/1          → Verify item details
4. POST /orders          → Create order with itemId = 1 (get orderId = 1)
5. GET /orders           → Verify order list
6. GET /orders/1         → Verify order details (status = "PENDING")
7. POST /payments/process → Process payment for orderId = 1 (get paymentId = 1)
8. GET /payments         → Verify payment list
9. GET /payments/1       → Verify payment details (status = "SUCCESS")
```

## Additional Resources

- **API Gateway**: Routes all requests to appropriate microservices
- **Docker Compose**: Manages all service containers
- **Spring Boot**: Each microservice is built with Spring Boot

## Questions?

If you encounter issues:
1. Check Docker container logs: `docker-compose logs [service-name]`
2. Verify all containers are running: `docker ps`
3. Restart services: `docker-compose restart`
4. Rebuild if needed: `docker-compose up --build`
