# Google Play Store Listing & Metadata — Housie Shopping

Complete store listing metadata, compliance questionnaire declarations, graphics specifications, and data safety entries for publishing **Housie Shopping: Home & Build** on the Google Play Console.

---

## 1. Store Listing Details

| Field | Requirement | Production Content |
|---|---|---|
| **App Name** | Max 30 characters | `Housie Shopping: Home & Build` (29 chars) |
| **Package Name** | Unique Android ID | `com.housieshopping.app` |
| **Short Description** | Max 80 characters | `Quality home building materials, cement, steel, tools & paints delivered fast.` (79 chars) |
| **Category** | Play Store category | **Shopping** |
| **Tags** | 5 Store Tags | Home Improvement, Building Materials, Shopping, Hardware, Construction |
| **Content Rating** | IARC | **Everyone** (PEGI 3, ESRB Everyone, USK 0) |
| **Target Audience** | Target Age Group | **18 and over** |

---

## 2. Full Description (Google Play Formatted, Max 4000 Characters)

```text
Build your dream home with confidence. Housie Shopping is India’s premier mobile destination for certified home construction materials, structural steel, premium cement, paints, hardware, and professional power tools — delivered straight to your building site.

Whether you are an individual home builder, architect, civil contractor, or renovation enthusiast, Housie Shopping connects you with verified manufacturers and wholesale depots with transparent pricing, instant GST invoicing, and live dispatch tracking.

🧱 COMPREHENSIVE BUILDING CATALOG
• Certified Cement: UltraTech, Ambuja, ACC, Shree Cement (OPC 53, PPC, PSC).
• Structural TMT Steel Rebars: Tata Tiscon, JSW Neosteel, Kamdhenu (Fe 500D, Fe 550D) in all standard diameters (8mm to 32mm).
• Wall Finishes & Paints: Asian Paints, Berger, Nerolac — interior emulsions, weather-shield exterior paints, primers, and waterproof putty.
• Bricks & Blocks: Kiln-fired red clay bricks, AAC lightweight blocks, and paver tiles.
• Plumbing & Sanitaryware: Jaquar, Astral, Ashirvad pipes, CPVC/UPVC fittings, bath accessories.
• Tools & Electricals: Bosch, Stanley power drills, angle grinders, Havells/Polycab copper wiring, and MCB panels.

⚡ WHY ORDER FROM HOUSIE SHOPPING?
✔ Direct Factory & Depot Pricing: Wholesale rates without middleman markup.
✔ Real-Time Stock Availability: Instant inventory checks with automated reserve holds.
✔ Verified Quality & Test Certificates: Mill test certificates available for bulk steel and cement.
✔ Free Delivery on Eligible Orders: Heavy freight delivery with GPS-guided transport.
✔ Live Vehicle Delivery Tracking: Follow your delivery truck on an interactive map in real-time.
✔ Multi-Payment Options: Pay securely via UPI, Credit/Debit Cards, NetBanking via Razorpay, or select Cash on Delivery (COD).
✔ Instant GST Invoicing: Download compliant tax invoices for commercial input credits.

📱 SEAMLESS SHOPPING EXPERIENCE
• Smart Search & Multi-Attribute Filters: Filter by brand, grade, size, and packaging unit.
• Interactive Site Location Pin: Pinpoint the exact gate or unload point on Google Maps.
• Order Timeline & Instant Alerts: Get notified from packing to site unloading.
• 24/7 Dedicated Support: In-app ticketing and phone support for material inquiries.

Download Housie Shopping today and elevate your home construction experience!
```

---

## 3. Graphical Asset Specifications

| Asset | Specifications | Production Path / Notes |
|---|---|---|
| **App Icon** | 512 x 512 px, 32-bit PNG with alpha, max 1024 KB | Generated from `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` |
| **Feature Graphic** | 1024 x 500 px, 24-bit PNG or JPEG, no transparency | `docs/assets/feature_graphic.png` (High-contrast Housie logo with construction backdrop) |
| **Phone Screenshots** | Minimum 4, Maximum 8.<br/>Format: 16:9 or 9:16 portrait (e.g. 1080 x 2400 px, PNG) | 1. Home Catalog & Categories<br/>2. TMT Steel & Cement Product Detail<br/>3. Real-Time Server Cart & GST Breakdown<br/>4. Interactive Map Site Drop Pin<br/>5. Live Order Tracking & Dispatch Timeline<br/>6. Profile, Addresses & Secure Keystore Auth |

---

## 4. Google Play Data Safety Section Declarations

Fill the Google Play Data Safety Questionnaire with the following audited entries:

### Does your app collect or share user data?
* **Answer:** **Yes**

### Is all of the user data collected by your app encrypted in transit?
* **Answer:** **Yes** (All API communication enforces TLS 1.3 / HTTPS).

### Do you provide a way for users to request that their data be deleted?
* **Answer:** **Yes** (In-app deletion via Profile -> Security -> Delete Account, and web deletion request at `https://housieshopping.com/account-deletion`).

### Data Types Breakdown:
1. **Personal Info:**
   - *Name, Email address, Phone number:* Collected for account management and order delivery coordination. Not shared with third-party advertisers.
2. **Location:**
   - *Approximate Location & Precise Location:* Collected in foreground to detect delivery region and locate delivery site.
3. **Financial Info:**
   - *Purchase History & Payment Status:* Collected for order fulfillment. No card/bank details are collected or stored by the app.
4. **App Info & Performance:**
   - *Crash logs & Diagnostics:* Collected to maintain app stability.

---

## 5. App Permissions Justification

| Permission | Justification for Google Play Reviewers |
|---|---|
| `INTERNET` | Required to communicate with the Housie Shopping production REST API backend and securely process transactions. |
| `ACCESS_NETWORK_STATE` | Required to monitor network connectivity and provide seamless offline Room database caching when connectivity is lost. |
| `ACCESS_FINE_LOCATION` | Required to allow the customer to pinpoint the exact site entrance/gate for heavy truck freight unloading on Google Maps. |
| `ACCESS_COARSE_LOCATION` | Required for low-power regional depot discovery and calculating estimated shipping timelines. |
| `POST_NOTIFICATIONS` | Required on Android 13+ to send critical order status updates (e.g., "Order Dispatched", "Out for Delivery", "Delivered"). |
