# 🛒 E-Commerce Backend API

A full-featured **RESTful backend** for an e-commerce platform (food delivery), built with **Spring Boot 3** and **Java 21**. This project demonstrates a production-ready architecture with JWT authentication, online payment integration, AI-powered product recommendations, and comprehensive admin management features.

> **Live Demo:** [bepviet.io.vn](https://www.bepviet.io.vn)

---

## ✨ Key Features

### 🔐 Authentication & Security
- JWT-based authentication with Spring Security
- Role-based access control (`USER`, `ADMIN`)
- Password reset via email with token-based verification

### 🛍️ Shopping Experience
- Product catalog with advanced search & filtering (name, category, price range)
- Pagination & sorting support across all list endpoints
- Shopping cart management (add, update, remove, clear)
- Product wishlist / like system
- Product review & rating system with moderation and rate limiting

### 📦 Order Management
- Full order lifecycle: `PENDING` → `CONFIRMED` → `SHIPPING` → `COMPLETED` / `CANCELLED`
- Multiple payment methods: COD, Stripe, SePay (Vietnamese bank transfer)
- Stripe Checkout integration with webhook verification
- Coupon / discount code system (order-wide, category-specific, or product-specific)
- Order confirmation emails with HTML templates (Thymeleaf)

### 🤖 AI-Powered Features
- AI product recommendation using **Spring AI** + **Google Gemini**
- Vector-based semantic search via **Pinecone** vector store
- Food advisor chatbot for personalized suggestions

### 📊 Admin Dashboard APIs
- Revenue statistics & analytics (overview, revenue by date, top products)
- Order management & status updates
- Product CRUD with image upload (Cloudinary)
- Banner & promotion banner management
- Coupon management with business rule validation
- Review moderation (approve, reject, reply)

### 📧 Email & Notifications
- Transactional emails: order confirmation, password reset
- Abandoned cart reminder (scheduled background job)
- Async email sending for non-blocking performance

---

## 🏗️ Tech Stack

| Layer              | Technology                                        |
|--------------------|---------------------------------------------------|
| **Framework**      | Spring Boot 3.5, Java 21                          |
| **Security**       | Spring Security, JWT (jjwt 0.11.5)                |
| **Database**       | PostgreSQL, Spring Data JPA, Hibernate             |
| **AI**             | Spring AI, Google Gemini, Pinecone Vector Store    |
| **Payment**        | Stripe API, SePay                                 |
| **File Upload**    | Cloudinary                                        |
| **Email**          | Spring Mail, Thymeleaf templates                  |
| **Validation**     | Spring Validation, Bean Validation                |

| **Mapping**        | MapStruct, Lombok                                 |
| **Containerization**| Docker (multi-stage build)                       |

---

## 📁 Project Structure

```
src/main/java/com/example/demo/
├── config/             # App configuration (Security, Cloudinary, AI, OpenAPI)
├── controller/         # REST API controllers (23 controllers)
├── dto/
│   ├── request/        # Request DTOs with validation
│   └── response/       # Response DTOs
├── entity/             # JPA entities (19 entities)
├── enums/              # Business enums (Role, OrderStatus, PaymentMethod, etc.)
├── event/              # Spring application events
├── exception/          # Global exception handling
├── mapper/             # MapStruct mappers
├── repository/         # Spring Data JPA repositories & specifications
├── security/           # JWT filter, service & user principal
├── service/            # Business logic layer (22 services)
└── utils/              # Utility classes
```

---

## 📖 API Overview

| Module                | Endpoints | Description                          |
|-----------------------|-----------|--------------------------------------|
| **Authentication**    | 4         | Register, login, password reset      |
| **Profile**           | 2         | View & update user profile           |
| **Address**           | 6         | CRUD + set default address           |
| **Products**          | 8+        | Search, CRUD, like                   |
| **Categories**        | 4         | CRUD categories                      |
| **Cart**              | 5         | Add, update, remove, clear cart      |
| **Orders**            | 5         | Create, list, detail, cancel orders  |
| **Payments**          | 3         | Stripe checkout, SePay, webhooks     |
| **Coupons**           | 7         | CRUD, activate, calculate discount   |
| **Reviews**           | 6         | Submit, list, moderate, reply        |
| **Banners**           | 4         | CRUD banners for storefront          |
| **Promotions**        | 4         | CRUD promotion banners               |
| **Admin Statistics**  | 4         | Revenue, top products, overview      |
| **AI Chat**           | 2         | Product recommendations, food advice |
| **Media**             | 1         | Image upload                         |
| **Users (Admin)**     | 4         | User management                      |

---

## 🔧 Architecture Highlights

- **Layered Architecture**: Controller → Service → Repository with clear separation of concerns
- **DTO Pattern**: Request/Response DTOs to decouple API contracts from entity models
- **MapStruct Mappers**: Compile-time, type-safe object mapping (zero reflection overhead)
- **Global Exception Handling**: Centralized error responses via `@RestControllerAdvice`
- **Spring Specifications**: Dynamic query building for flexible product & review filtering
- **Event-Driven**: Spring Application Events for async review stats recalculation
- **Scheduled Tasks**: Background jobs for abandoned cart reminders
- **Multi-stage Docker Build**: Optimized container image with build and runtime separation

---

## 📝 License

This project is for educational and portfolio purposes.
