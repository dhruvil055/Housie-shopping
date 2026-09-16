# Housie Shopping — Production Deployment Guide

Comprehensive DevOps and deployment manual for deploying the **Housie Shopping** production backend and publishing the signed Android App Bundle (`.aab`) to the **Google Play Store**.

---

## 1. Production Architecture Overview

```text
Google Play Store
      │ (Signed .aab - Target SDK 34)
      ▼
Housie Customer Android App ──(HTTPS/TLS 1.3)──┐
                                               ▼
Housie Admin Android App ─────(HTTPS/TLS 1.3)──┤
                                               ▼
                                 Reverse Proxy (Nginx / Cloudflare SSL)
                                               │
                                               ▼
                                   Node.js / Express REST API
                                        (Docker / PM2)
                                         ├── Razorpay Gateway (HMAC Webhooks)
                                         └── MongoDB Database (Mongoose Replica)
```

---

## 2. Backend API & Database Deployment

### Prerequisites
* Linux Server (Ubuntu 22.04 LTS / Debian 12 / AWS EC2 / DigitalOcean Droplet)
* Docker & Docker Compose OR Node.js v20+ with PM2
* MongoDB 7+ (or MongoDB Atlas Cloud Cluster with M10+ replica set)
* Domain name configured with SSL (e.g., `api.housieshopping.com`)

### Step 2.1: Clone and Configure Environment
On your production server:
```bash
git clone https://github.com/dhruvil055/Housie-shopping.git
cd Housie-shopping/backend
cp .env.example .env
nano .env
```

Set the production variables:
```ini
NODE_ENV=production
PORT=5000
MONGO_URI=mongodb+srv://housie_prod_user:<PASSWORD>@cluster0.mongodb.net/housie_production?retryWrites=true&w=majority
JWT_SECRET=production_super_strong_jwt_secret_key_minimum_64_chars_hex
JWT_REFRESH_SECRET=production_super_strong_jwt_refresh_secret_key_minimum_64_chars_hex
JWT_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=7d
RAZORPAY_KEY_ID=rzp_live_xxxxxxxxxxxxxx
RAZORPAY_KEY_SECRET=live_secret_xxxxxxxxxxxxxx
RAZORPAY_WEBHOOK_SECRET=live_webhook_secret_xxxxxxxxxxxxxx
CORS_ORIGIN=*
```

### Step 2.2: Launch via Docker
From the project root:
```bash
docker compose up -d --build
```
Or with PM2 directly:
```bash
cd backend
npm install --production
npm run seed  # Seed initial categories, catalog, and super admin
pm2 start src/server.js --name "housie-api" -i max
pm2 save
pm2 startup
```

### Step 2.3: Configure Nginx Reverse Proxy with TLS 1.3
`/etc/nginx/sites-available/housie-api.conf`:
```nginx
server {
    listen 80;
    server_name api.housieshopping.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.housieshopping.com;

    ssl_certificate /etc/letsencrypt/live/api.housieshopping.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.housieshopping.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;

    location / {
        proxy_pass http://127.0.0.1:5000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

---

## 3. Android Release Build & Signing

### Step 3.1: Release Keystore Setup
Ensure `keystore.properties` is configured at the root of the project:
```properties
storeFile=../housie-release.keystore
storePassword=<YOUR_SECURE_STORE_PASSWORD>
keyAlias=housieshopping
keyPassword=<YOUR_SECURE_KEY_PASSWORD>
```
To generate a fresh 4096-bit release keystore:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\generate-release-keystore.ps1
```

### Step 3.2: Generate Signed Customer App Bundle (`.aab`)
Google Play strictly requires the `.aab` (Android App Bundle) format:
```powershell
.\gradlew.bat :app:bundleRelease
```
The resulting signed production bundle will be located at:
```text
app/build/outputs/bundle/release/app-release.aab
```

### Step 3.3: Generate Signed Admin App Bundle
For enterprise distribution or private track publishing:
```powershell
.\gradlew.bat :admin:bundleRelease
```
The resulting signed bundle will be located at:
```text
admin/build/outputs/bundle/release/admin-release.aab
```

---

## 4. Google Play Console Submission Step-by-Step

### Step 4.1: Create Application
1. Sign in to [Google Play Console](https://play.google.com/console).
2. Click **Create app**.
3. **App details**:
   - App name: `Housie Shopping: Home & Build`
   - Default language: `English (United States)`
   - App or game: `App`
   - Free or paid: `Free`
4. Accept the declarations and click **Create app**.

### Step 4.2: Play App Signing
1. Navigate to **Release > Setup > App integrity**.
2. Keep the recommended **Google Play App Signing** enabled. Google will re-sign the app with Google's cloud key while our generated upload key authenticates the upload.

### Step 4.3: App Content & Policies
Navigate to **Policy and programs > App content** and complete:
1. **Privacy Policy**: Paste `https://housieshopping.com/privacy-policy` (sourced from `docs/PRIVACY_POLICY.md`).
2. **Data Safety Questionnaire**: Complete with the audited declarations in `docs/PLAY_STORE_METADATA.md#4-google-play-data-safety-section-declarations`.
3. **Account Deletion URL**: Enter `https://housieshopping.com/account-deletion` (also supported directly in-app under Profile Settings).
4. **Target Audience**: Select **18 and over**.
5. **Government Apps / Financial Features**: Declare as regular e-commerce shopping app.
6. **Advertising ID**: Declare that the app does not use Advertising ID for targeted ad profiling.

### Step 4.4: Store Presence
1. Navigate to **Grow users > Store presence > Main store listing**.
2. Fill **Short description** and **Full description** from `docs/PLAY_STORE_METADATA.md`.
3. Upload **App Icon** (512x512) and **Feature Graphic** (1024x500).
4. Upload at least 4 phone screenshots (1080x2400).

### Step 4.5: Upload Release Bundle
1. Navigate to **Release > Testing > Internal testing** (or **Production**).
2. Click **Create new release**.
3. Drag and drop `app-release.aab`.
4. Release name: `1.0.0 (1)`
5. Release notes:
   ```text
   Initial production release of Housie Shopping:
   • Complete building materials catalog (Cement, TMT Steel, Paints, Tools)
   • Secure Razorpay payments and Cash on Delivery
   • Live vehicle dispatch tracking with interactive site drop pins
   • In-app customer support and tax invoice downloads
   ```
6. Click **Save** and **Review release**.
7. Click **Start rollout to Internal testing** (or submit for Production review).
