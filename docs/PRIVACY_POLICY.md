# Privacy Policy for Housie Shopping

**Effective Date:** September 16, 2026  
**Last Updated:** September 16, 2026  
**Application:** Housie Shopping: Home & Build (Package: `com.housieshopping.app`)  
**Publisher:** Housie Shopping Pvt. Ltd.  

---

## 1. Introduction

Housie Shopping ("we", "our", or "us") is dedicated to protecting your privacy. This Privacy Policy governs your use of the **Housie Shopping: Home & Build** mobile Android application and our backend application services. It explains what personal data we collect, how we handle, process, and protect that data, and how you can exercise your privacy and data deletion rights in accordance with the Google Play Developer Distribution Agreement and User Data policies.

By downloading, installing, or using the Housie Shopping Android application, you consent to the practices described in this Privacy Policy.

---

## 2. Information We Collect and Why

We collect only information necessary to deliver quality home building materials, cement, steel, structural hardware, fixtures, and tools directly to your designated delivery site.

### A. Personal Identification & Contact Information
* **Information Collected:** Full name, email address, mobile phone number, profile avatar.
* **Purpose:** To create your customer account, authenticate your login credentials, verify your identity via One-Time Password (OTP), send order confirmations, and coordinate logistics with delivery drivers.
* **Data Storage:** Stored securely on our cloud servers using cryptographic salting and hashing (bcrypt). Stored locally on your device only in encrypted storage (`EncryptedSharedPreferences`) backed by the hardware Android Keystore.

### B. Delivery Site & Address Information
* **Information Collected:** Street addresses, landmarks, city, state, postal PIN code, recipient name, and contact phone number.
* **Purpose:** To calculate logistics fees, estimate delivery timelines, and dispatch heavy construction materials to your construction site or residence.

### C. Precise and Approximate Location Data
* **Information Collected:** Latitude and longitude coordinates (via `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`).
* **Purpose:**
  1. To automatically detect your delivery area and display available materials in your regional depot.
  2. To display interactive Google Maps during checkout to pinpoint exact gate/site access.
  3. To enable real-time tracking of delivery trucks while your order is out for delivery.
* **Background Location:** Housie Shopping **DOES NOT** access or record your location in the background when the application is closed. Location data is accessed solely in the foreground while actively interacting with maps or tracking an active order.

### D. Payment & Financial Data
* **Information Collected:** Transaction ID, order ID, payment status (PAID/FAILED), payment method selected (UPI, Credit/Debit Card, NetBanking, Cash on Delivery).
* **Payment Card & Bank Account Data:** Housie Shopping **NEVER collects, stores, or processes your credit card numbers, debit card numbers, CVVs, or UPI PINs**. All electronic payments are processed directly through our secure, PCI-DSS certified payment gateway partner (**Razorpay**).

### E. Device & Diagnostic Data
* **Information Collected:** Device manufacturer, OS version, app version code, network connectivity status, crash telemetry.
* **Purpose:** To diagnose software crashes, prevent fraud, optimize app performance across Android devices, and ensure compatibility with modern Android OS releases (Android 14 / 15 / 16).

---

## 3. Third-Party Services and SDKs

We work with trusted third-party services that process data strictly on our behalf under industry standard security and privacy agreements:

| Service / SDK | Provider | Purpose | Privacy Policy Link |
|---|---|---|---|
| **Razorpay** | Razorpay Software Pvt. Ltd. | PCI-DSS Level 1 payment gateway processing for UPI, Cards, NetBanking | [Razorpay Privacy Policy](https://razorpay.com/privacy/) |
| **Google Maps Platform** | Google LLC | Interactive maps for site drop location and live order vehicle routing | [Google Privacy Policy](https://policies.google.com/privacy) |
| **Google Play Services** | Google LLC | Location providers, app distribution, and security verification | [Google Play Services Privacy](https://policies.google.com/privacy) |

---

## 4. How We Protect Your Data (Security)

We adhere to modern enterprise security practices to ensure your information is safeguarded against unauthorized access, alteration, or disclosure:
* **Encryption in Transit:** All communications between the Housie Shopping Android app and our backend APIs are strictly encrypted using TLS 1.3 / HTTPS. Cleartext HTTP traffic is disabled.
* **Hardware-Backed Encryption at Rest:** Authentication tokens (JWT) and user session state are encrypted locally using AES-256-GCM via Android Jetpack Security (`MasterKey` and `EncryptedSharedPreferences`) backed by the Android Keystore hardware security module.
* **Backup Isolation:** Encrypted preferences containing sensitive keys are explicitly excluded from cloud backup to prevent unauthorized extraction across device restorations.
* **Database Isolation:** Production databases enforce strict role-based access control (RBAC) and audit logging for all administrative operations.

---

## 5. Account & Personal Data Deletion (Google Play Compliance)

You have the unconditional right to request the permanent deletion of your Housie Shopping account and all associated personal data at any time.

### Option 1: In-App Account Deletion
1. Open the Housie Shopping app and sign in.
2. Tap the **Profile** tab in the bottom navigation bar.
3. Select **Security & Account Settings**.
4. Tap **Delete Account Permanently** and confirm your password or OTP.
5. The app immediately calls `DELETE /api/v1/users/me`, terminates your active session, purges local cache, and permanently removes your profile, saved addresses, cart, and wishlist from our production database.

### Option 2: Web-Based Account Deletion Request
If you have uninstalled the app or cannot access your device, you can request account deletion via our web portal:
* **Account Deletion URL:** `https://housieshopping.com/account-deletion`
* **Direct Email Request:** Send an email from your registered address to `privacy@housieshopping.com` with the subject line *"Account Deletion Request"*.

### Data Retention and Deletion Timeline:
* **Immediate Purge:** Your user account, password hash, authentication tokens, profile photo, and delivery addresses are purged immediately upon confirmation.
* **Regulatory Exemption:** As required by Indian law (Goods and Services Tax Act and commercial bookkeeping regulations), completed sales invoices and order financial summaries are retained for statutory tax audit periods (typically 7 years) in an anonymized format with personal contact links detached.

---

## 6. Children's Privacy

Housie Shopping does not knowingly collect personal information from individuals under 18 years of age. If we learn that a user under 18 has registered an account, we will immediately delete that account and associated data.

---

## 7. Changes to This Privacy Policy

We may update this Privacy Policy from time to time. Any material changes will be announced via an in-app update prompt or notification. The "Last Updated" date at the top will reflect the current version.

---

## 8. Grievance Officer & Contact Information

For any questions, concerns, or requests regarding this Privacy Policy or your personal data, contact:

* **Grievance Officer:** Grievance Officer, Housie Shopping Pvt. Ltd.  
* **Office Address:** Housie Shopping Tower, S.G. Highway, Ahmedabad, Gujarat 380054, India  
* **Email:** `privacy@housieshopping.com` or `support@housieshopping.com`  
* **Customer Support Helpline:** +91 79 4000 5500  
