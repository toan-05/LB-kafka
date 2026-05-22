# Order Service

Event-driven order processing service built with Spring Boot, MySQL, Flyway, Kafka, Docker Compose, and Nginx.

The service accepts orders, publishes an order-created Kafka event, reserves inventory asynchronously, publishes the inventory result, and updates the order status from that result.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web, Spring Data JPA, Validation, Actuator
- MySQL 8
- Flyway
- Apache Kafka
- Docker Compose
- Nginx load balancing
- Swagger UI / OpenAPI

## Architecture

```text
Client
  |
  v
Nginx (:80)
  |
  +--> app1 (:8081)
  |
  +--> app2 (:8082)
          |
          v
        MySQL
          ^
          |
Kafka topics:
  order-created     -> InventoryConsumer reserves stock
  inventory-result  -> OrderStatusConsumer updates order status
```

## Main Flow

1. Client creates an order through `POST /api/v1/orders`.
2. The service stores an order snapshot with status `CREATED`.
3. The service publishes an `order-created` Kafka event.
4. The inventory consumer reserves stock using pessimistic locking.
5. The inventory reservation is stored in `inventory_reservations` so duplicate Kafka deliveries do not deduct stock twice.
6. The service publishes an `inventory-result` event.
7. The order status consumer updates the order to `INVENTORY_RESERVED` or `INVENTORY_REJECTED`.

## API

Swagger UI is available after startup:

- `http://localhost/swagger-ui.html` through Nginx
- `http://localhost:8081/swagger-ui.html` for app1
- `http://localhost:8082/swagger-ui.html` for app2

Useful endpoints:

```http
GET /api/v1/products
POST /api/v1/products
GET /api/v1/products/{id}
PUT /api/v1/products/{id}
DELETE /api/v1/products/{id}

GET /api/v1/orders
POST /api/v1/orders
GET /api/v1/orders/{id}
PUT /api/v1/orders/{id}
DELETE /api/v1/orders/{id}

GET /api/v1/inventory-reservations
GET /api/v1/inventory-reservations/{orderId}
DELETE /api/v1/inventory-reservations/{orderId}

GET /health
```

Create order example:

```bash
curl -X POST http://localhost/api/v1/orders \
  -H "Content-Type: application/json" \
  -d "{\"productId\":1,\"quantity\":2}"
```

Create product example:

```bash
curl -X POST http://localhost/api/v1/products \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Mechanical Keyboard\",\"price\":99.00,\"stockQuantity\":10}"
```

## Run Locally

Create your local environment file:

```bash
cp .env.example .env
```

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Start the full stack:

```bash
docker compose up --build
```

Services:

- API through Nginx: `http://localhost`
- app1: `http://localhost:8081`
- app2: `http://localhost:8082`
- Kafka UI: `http://localhost:8080`
- MySQL: `localhost:3306`

Stop the stack:

```bash
docker compose down
```

Remove database volume:

```bash
docker compose down -v
```

## Test

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Notes For Reviewers

- Inventory reservation uses pessimistic locking to prevent concurrent stock overselling.
- Kafka inventory processing is idempotent per order through the `inventory_reservations` table.
- Flyway owns the schema and JPA runs with `ddl-auto=validate`.
- Docker Compose starts two service instances behind Nginx to demonstrate horizontal scaling and consumer group behavior.
