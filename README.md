# 🛍️ MarketHub — Mini Marketplace

A full-stack Spring Boot marketplace with role-based access, Docker, and CI/CD pipeline.

**Live URL:** `https://your-app.onrender.com`  
**GitHub:** `https://github.com/your-org/mini-marketplace`

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                   Browser                   │
└──────────────────────┬──────────────────────┘
                       │ HTTP
┌──────────────────────▼──────────────────────┐
│           Spring Boot Application            │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  │
│  │  Auth    │  │ Product  │  │   Admin   │  │
│  │Controller│  │Controller│  │Controller │  │
│  └────┬─────┘  └────┬─────┘  └─────┬─────┘  │
│       │             │              │         │
│  ┌────▼─────────────▼──────────────▼─────┐  │
│  │           Service Layer                │  │
│  │  UserService  ProductService  Order    │  │
│  └────────────────────┬───────────────────┘  │
│                       │ JPA                  │
│  ┌────────────────────▼───────────────────┐  │
│  │          Repository Layer              │  │
│  └────────────────────┬───────────────────┘  │
└───────────────────────┼─────────────────────┘
                        │
┌───────────────────────▼─────────────────────┐
│              PostgreSQL Database             │
└─────────────────────────────────────────────┘
```

---

## ER Diagram

```
app_users
─────────────────────
PK  id          BIGINT
    username    VARCHAR (UNIQUE)
    password    VARCHAR
    role        VARCHAR (BUYER/SELLER/ADMIN)

product
─────────────────────
PK  id          BIGINT
    name        VARCHAR
    price       DOUBLE
    description VARCHAR
    category    VARCHAR
    stock       INTEGER

order_entity
─────────────────────
PK  id          BIGINT
FK  buyer_id    → app_users.id   (M:1)
FK  product_id  → product.id     (M:1)
    quantity    INTEGER

Relationships:
  app_users 1 ──< order_entity >── 1 product
```

---

## Roles & Access

| URL Pattern         | BUYER | SELLER | ADMIN |
|---------------------|-------|--------|-------|
| GET /products       | ✅    | ✅     | ✅    |
| POST /products      | ❌    | ✅     | ✅    |
| POST /products/delete | ❌  | ✅     | ✅    |
| GET /orders         | ✅    | ❌     | ✅    |
| POST /orders/place  | ✅    | ❌     | ✅    |
| GET /admin/**       | ❌    | ❌     | ✅    |

---

## API Endpoints

### Auth
| Method | URL           | Description        | Auth Required |
|--------|---------------|--------------------|---------------|
| GET    | /login        | Login page         | No            |
| POST   | /login        | Authenticate user  | No            |
| GET    | /register     | Register page      | No            |
| POST   | /register     | Create account     | No            |
| POST   | /logout       | Logout             | Yes           |

### Products
| Method | URL                    | Description         | Role          |
|--------|------------------------|---------------------|---------------|
| GET    | /products              | List all products   | Any           |
| POST   | /products              | Add product         | SELLER/ADMIN  |
| GET    | /products/edit/{id}    | Edit form           | SELLER/ADMIN  |
| POST   | /products/edit/{id}    | Save edit           | SELLER/ADMIN  |
| POST   | /products/delete/{id}  | Delete product      | SELLER/ADMIN  |

### Orders
| Method | URL                  | Description     | Role   |
|--------|----------------------|-----------------|--------|
| GET    | /orders              | My orders       | BUYER  |
| POST   | /orders/place        | Place order     | BUYER  |
| POST   | /orders/cancel/{id}  | Cancel order    | BUYER  |

### Admin
| Method | URL                      | Description    | Role  |
|--------|--------------------------|----------------|-------|
| GET    | /admin/dashboard         | Dashboard      | ADMIN |
| POST   | /admin/users/delete/{id} | Delete user    | ADMIN |

---

## Run Instructions

### Using Docker (Recommended)
```bash
git clone https://github.com/your-org/mini-marketplace
cd mini-marketplace
docker compose up --build
```
Visit: http://localhost:8080

### Local Development
```bash
# Start PostgreSQL
docker run -d -e POSTGRES_DB=market -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:15

# Run app
./mvnw spring-boot:run
```

### Environment Variables
| Variable                  | Default                              | Description        |
|---------------------------|--------------------------------------|--------------------|
| SPRING_DATASOURCE_URL     | jdbc:postgresql://db:5432/market     | DB connection URL  |
| SPRING_DATASOURCE_USERNAME| postgres                             | DB username        |
| SPRING_DATASOURCE_PASSWORD| password                             | DB password        |
| PORT                      | 8080                                 | App port           |

---

## CI/CD Pipeline

```
Push to feature/* branch
        │
        ▼
Pull Request → develop
        │
        ▼
GitHub Actions: build + test (JUnit + MockMvc)
        │
Pull Request → main (requires 1 approval)
        │
        ▼
GitHub Actions: build + test + deploy to Render
```

**GitHub Secrets required:**
- `RENDER_API_KEY` — from Render dashboard
- `RENDER_SERVICE_ID` — your Render service ID

---

## Testing

```bash
mvn test
```

- **15 Unit Tests** — ProductService (findAll, save, delete, search, filter, update, count...)
- **3 Integration Tests** — ProductController (GET /products, POST /products, DELETE, filters)

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, HTML/CSS (dark gold theme)
- **Database:** PostgreSQL 15
- **Testing:** JUnit 5, Mockito, MockMvc, H2 (in-memory for CI)
- **DevOps:** Docker, Docker Compose, GitHub Actions, Render
