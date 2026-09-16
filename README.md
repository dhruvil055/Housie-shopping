# Housie Shopping — Enterprise E-Commerce Platform

A production-grade, secure, scalable home-building-material e-commerce platform consisting of:
1. **Customer Native Android Application** (`app/`): 55+ screens built with Jetpack Compose Material 3, Clean Architecture, Room caching, and Razorpay payments.
2. **Admin Native Android Application** (`admin/`): Dedicated operations app for catalog management, stock control, order status updates, logistics tracking, coupons, and audit logs.
3. **Headless Production REST API & Backend** (`backend/`): Scalable Node.js / Express backend with MongoDB, JWT rotation, RBAC, HMAC-SHA256 payment verification, idempotency keys, and automated seeder.

---

## 🏛 System Architecture

```
                                  +-----------------------+
                                  |    MongoDB Cluster    |
                                  +-----------+-----------+
                                              |
                                              | Mongoose ODM
                                              v
                               +-----------------------------+
                               |   Housie Headless REST API  |
                               |   (Node.js / Express / ES)  |
                               +--------------+--------------+
                                              |
                   +--------------------------+--------------------------+
                   | (Bearer JWT Token)                                  | (Bearer JWT Token)
                   v                                                     v
    +------------------------------+                      +------------------------------+
    |    Customer Android App      |                      |      Admin Android App       |
    |           (`app/`)           |                      |          (`admin/`)          |
    +------------------------------+                      +------------------------------+
    | • Jetpack Compose M3         |                      | • Jetpack Compose M3         |
    | • Clean Architecture + MVVM  |                      | • Clean Architecture + MVVM  |
    | • Room Local Database        |                      | • Room Local Database        |
    | • EncryptedSharedPreferences |                      | • Secure DataStore Prefs     |
    | • Razorpay Payment Gateway   |                      | • Order Dispatch & Tracking  |
    | • Offline-First Fallbacks    |                      | • Offline-First Fallbacks    |
    +------------------------------+                      +------------------------------+
```

---

## 🚀 Key Features

### 1. Customer Android App (`app/`)
- **55+ Reactive Screens**: Startup splash, authentication (login/register/forgot PIN), location picker, home feed with deals, category browser, instant search with auto-suggest, multi-parameter filter drawer, detailed product page with variants and reviews, interactive cart, wishlist, multi-address manager, order confirmation, live delivery tracking with map markers, PDF invoices, support tickets, and profile.
- **Security & Keystore**: Android Keystore `EncryptedSharedPreferences` for access and refresh token storage; automatic token rotation via OkHttp `Authenticator`.
- **Payment & Checkout**: Multi-step checkout with server-side pricing recalculation, GST computation, coupon validation, inventory reservation, and Razorpay HMAC-SHA256 signature verification.
- **Resilience**: Room database offline caching with remote-first fallback to ensure zero UI freezes or crashes in poor connectivity.

### 2. Admin Android App (`admin/`)
- **Real-Time Operations Dashboard**: Daily, weekly, and monthly revenue metrics, pending order counts, out-of-stock / low-stock counters, and quick actions.
- **Catalog Management**: Add, update, delete, and toggle stock state for construction materials with SKU auto-generation and image support.
- **Order Logistics & Tracking**: Move order status (`Pending` -> `Confirmed` -> `Packing` -> `In Transit` -> `Delivered`), assign courier partner, and set tracking numbers.
- **Promotions & Support**: Manage discount coupons, promotional home banners, customer accounts, and resolve customer support tickets.
- **Immutable Audit Trail**: Automatic logging of all administrative actions with timestamp and user attribution.

### 3. Production REST Backend (`backend/`)
- **Modular Controller-Service Architecture**: ES modules with controllers for `auth`, `users`, `products`, `cart`, `wishlist`, `orders`, and `admin`.
- **Security & RBAC**: Helmet security headers, CORS origin protection, `express-rate-limit` DDoS prevention, Joi schema validation, and role-based access control (`customer`, `staff`, `manager`, `admin`, `super_admin`).
- **Idempotency**: `X-Idempotency-Key` middleware preventing duplicate charges or orders during network retries.
- **Logging**: Winston logger formatting structured request/response logs.
- **Seeder**: Realistic construction material catalog (cement, steel rebar, paints, bricks) with pre-configured admin accounts and test coupons.

---

## 🔒 Security Architecture

| Security Domain | Implementation |
|---|---|
| **Customer Token Storage** | Android Keystore `EncryptedSharedPreferences` (AES-256-GCM + RSA-OAEP) |
| **Admin Token Storage** | Jetpack DataStore Preferences with secure session management |
| **Transport Security** | Network Security Config enforcing HTTPS / TLS 1.3 (with dev exception for emulator `10.0.2.2`) |
| **Authentication** | JWT Access Token (15m expiry) + Refresh Token Rotation (7d expiry, stored in DB) |
| **Authorization** | Strict RBAC middleware protecting sensitive administration and inventory endpoints |
| **Payment Verification** | Cryptographic HMAC-SHA256 signature verification against Razorpay secret key |
| **Financial Integrity** | Server-side cart recalculation (ignoring client-tampered prices/totals) |
| **Double-Click Protection** | Idempotency keys (`X-Idempotency-Key`) stored in DB index |
| **Code Obfuscation** | Enhanced ProGuard / R8 rules for shrinking and securing release builds |

---

## 🛠 Quick Start Guide

### Prerequisites
- **Node.js**: v20+
- **MongoDB**: v7+ (or Docker)
- **Java Development Kit**: JDK 17
- **Android Studio**: Iguana (2023.2.1) or newer
- **Android SDK**: API 34

---

### Step 1: Start the Backend & Database

#### Option A: Using Docker Compose (Recommended)
```bash
docker-compose up -d
```
The API will start at `http://localhost:5000` with an attached MongoDB instance.

#### Option B: Local Node.js
```bash
cd backend
npm install
cp .env.example .env

# Seed initial catalog, categories, admin users, and coupons
npm run seed

# Start server in development mode
npm run dev
```

### Step 2: Run Backend Tests
```bash
cd backend
npm test
```
*Runs 4 automated Jest test suites (Authentication, RBAC, Cart Pricing, Order & Payment Verification).*

---

### Step 3: Run Android Unit Tests
```bash
# From the project root
./gradlew.bat testDebugUnitTest
```
*Executes unit tests for both `:app` (pricing calculations, cart stock validations, product filters) and `:admin`.*

---

### Step 4: Build & Run Android Applications

#### Customer App
```bash
./gradlew.bat :app:assembleDebug
```
Output APK: `app/build/outputs/apk/debug/app-debug.apk`

#### Admin App
```bash
./gradlew.bat :admin:assembleDebug
```
Output APK: `admin/build/outputs/apk/debug/admin-debug.apk`

*Default Admin Security PIN: `1234`*

---

## 📡 API Endpoints Reference

### Public & Authentication
- `POST /api/v1/auth/register` — Register a customer account
- `POST /api/v1/auth/login` — Login with email/password (returns access + refresh tokens)
- `POST /api/v1/auth/refresh-token` — Rotate tokens using refresh token
- `POST /api/v1/auth/logout` — Revoke active refresh token

### Catalog & Products
- `GET /api/v1/products` — Filter, search, and paginate products
- `GET /api/v1/products/:id` — Retrieve product details and variants
- `GET /api/v1/categories` — List active categories
- `GET /api/v1/banners` — List active promotion banners

### Cart & Checkout
- `GET /api/v1/cart` — Fetch customer cart
- `POST /api/v1/cart/items` — Add item / variant to cart
- `PATCH /api/v1/cart/items/:id` — Update quantity or save for later
- `DELETE /api/v1/cart/items/:id` — Remove item from cart
- `POST /api/v1/cart/apply-coupon` — Validate and apply discount coupon

### Orders & Payments
- `POST /api/v1/orders/create-payment-order` — Create Razorpay order & reserve stock
- `POST /api/v1/orders/verify-payment` — HMAC verification, idempotency check, and order placement
- `GET /api/v1/orders` — List customer orders
- `GET /api/v1/orders/:id` — Get order status and tracking details

### Admin Backoffice (Protected by Admin RBAC)
- `POST /api/v1/admin/login` — Admin authentication
- `GET /api/v1/admin/analytics` — Dashboard metrics & sales summary
- `GET /api/v1/admin/products` — Manage products
- `POST /api/v1/admin/products` — Create product
- `PUT /api/v1/admin/products/:id` — Update product
- `DELETE /api/v1/admin/products/:id` — Soft-delete / deactivate product
- `GET /api/v1/admin/orders` — List all orders across customers
- `PATCH /api/v1/admin/orders/:id/status` — Update order status, tracking number, and courier
- `GET /api/v1/admin/customers` — List registered customers and aggregate spend
- `PATCH /api/v1/admin/customers/:id/status` — Toggle customer suspension
- `GET /api/v1/admin/coupons` — List and create promotional coupons
- `GET /api/v1/admin/audit-logs` — Inspect administrative audit trail

---

## 🧪 CI/CD Pipeline

The `.github/workflows/ci.yml` pipeline automatically triggers on push and pull requests to `master` and `main`:
1. **Backend CI**: Launches MongoDB service container, installs dependencies, and runs the entire Jest test suite.
2. **Android CI**: Sets up JDK 17, executes `./gradlew testDebugUnitTest`, builds both `:app:assembleDebug` and `:admin:assembleDebug`, and publishes the debug APK artifacts.

---

## 📄 License
Commercial proprietary software developed for Housie Shopping. All rights reserved.
