import request from 'supertest';
import { createApp } from '../src/app.js';
import { connectTestDB, closeTestDB, clearTestDB } from './setup.js';
import { User } from '../src/models/User.js';
import { Product } from '../src/models/Product.js';
import { Notification } from '../src/models/Notification.js';

const app = createApp();

beforeAll(async () => {
  await connectTestDB();
});

afterAll(async () => {
  await closeTestDB();
});

beforeEach(async () => {
  await clearTestDB();
});

describe('Inventory & Notification & Payment API Tests', () => {
  let customerToken;
  let adminToken;
  let testProduct;

  beforeEach(async () => {
    // Register customer
    const custRes = await request(app)
      .post('/api/v1/auth/register')
      .send({
        name: 'Inventory Test User',
        email: 'invuser@example.com',
        phone: '+91 99999 11111',
        password: 'password123'
      });
    customerToken = custRes.body.data.accessToken;

    // Create admin user directly
    const admin = await User.create({
      name: 'Admin User',
      email: 'admin@housietest.com',
      phone: '+91 99999 22222',
      password: 'adminPassword123',
      role: 'ADMIN'
    });

    const adminLoginRes = await request(app)
      .post('/api/v1/auth/login')
      .send({
        emailOrPhone: 'admin@housietest.com',
        password: 'adminPassword123'
      });
    adminToken = adminLoginRes.body.data.accessToken;

    // Create product
    testProduct = await Product.create({
      sku: 'TEST-STEEL-01',
      title: 'Test TMT Rebars 500D',
      description: 'Heavy duty testing steel',
      brand: 'Tata Steel',
      categoryName: 'Steel & Iron',
      price: 500,
      mrp: 600,
      stock: 50,
      reservedStock: 0,
      isActive: true
    });
  });

  it('should check real-time stock availability', async () => {
    const res = await request(app)
      .post('/api/v1/inventory/check')
      .send({
        items: [
          { productId: testProduct._id.toString(), quantity: 10 }
        ]
      });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.allAvailable).toBe(true);
    expect(res.body.data.items[0].availableQuantity).toBe(50);
  });

  it('should reserve and release stock atomically', async () => {
    // Reserve 15 units
    const reserveRes = await request(app)
      .post('/api/v1/inventory/reserve')
      .set('Authorization', `Bearer ${customerToken}`)
      .send({
        items: [{ productId: testProduct._id.toString(), quantity: 15 }]
      });

    expect(reserveRes.status).toBe(200);
    expect(reserveRes.body.success).toBe(true);

    const updated = await Product.findById(testProduct._id);
    expect(updated.reservedStock).toBe(15);

    // Release 15 units
    const releaseRes = await request(app)
      .post('/api/v1/inventory/release')
      .set('Authorization', `Bearer ${customerToken}`)
      .send({
        items: [{ productId: testProduct._id.toString(), quantity: 15 }]
      });

    expect(releaseRes.status).toBe(200);
    const released = await Product.findById(testProduct._id);
    expect(released.reservedStock).toBe(0);
  });

  it('should query low-stock products as admin', async () => {
    // Set stock below threshold
    testProduct.stock = 5;
    await testProduct.save();

    const res = await request(app)
      .get('/api/v1/inventory/low-stock?threshold=10')
      .set('Authorization', `Bearer ${adminToken}`);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.items.length).toBeGreaterThan(0);
    expect(res.body.data.items[0].sku).toBe('TEST-STEEL-01');
  });

  it('should fetch user notifications and mark them as read', async () => {
    // Create direct and broadcast notification
    await Notification.create([
      {
        title: 'Welcome to Housie!',
        message: 'Your account is ready.',
        type: 'SYSTEM'
      }
    ]);

    const res = await request(app)
      .get('/api/v1/notifications')
      .set('Authorization', `Bearer ${customerToken}`);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.notifications.length).toBe(1);
    expect(res.body.data.unreadCount).toBe(1);

    const notifId = res.body.data.notifications[0]._id;

    // Mark as read
    const readRes = await request(app)
      .patch(`/api/v1/notifications/${notifId}/read`)
      .set('Authorization', `Bearer ${customerToken}`);

    expect(readRes.status).toBe(200);
    expect(readRes.body.data.isRead).toBe(true);
  });

  it('should discover payment methods and handle Razorpay webhook safely', async () => {
    // Check payment methods
    const methodsRes = await request(app).get('/api/v1/payments/methods');
    expect(methodsRes.status).toBe(200);
    expect(methodsRes.body.data.methods.length).toBe(2);

    // Test webhook endpoint
    const webhookRes = await request(app)
      .post('/api/v1/payments/webhook')
      .send({
        event: 'payment.captured',
        payload: {
          payment: {
            entity: { id: 'pay_test_12345', order_id: 'order_test_999' }
          }
        }
      });

    expect(webhookRes.status).toBe(200);
    expect(webhookRes.body.success).toBe(true);
  });
});
