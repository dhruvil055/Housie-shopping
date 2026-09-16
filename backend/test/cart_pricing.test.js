import request from 'supertest';
import { createApp } from '../src/app.js';
import { connectTestDB, closeTestDB, clearTestDB } from './setup.js';
import { Product } from '../src/models/Product.js';
import { Coupon } from '../src/models/Coupon.js';

const app = createApp();
let authToken;
let testProduct;

beforeAll(async () => {
  await connectTestDB();
});

afterAll(async () => {
  await closeTestDB();
});

beforeEach(async () => {
  await clearTestDB();

  // Create test user and get auth token
  const regRes = await request(app)
    .post('/api/v1/auth/register')
    .send({
      name: 'Cart Tester',
      email: 'cart@example.com',
      phone: '+91 90000 11111',
      password: 'password123'
    });
  authToken = regRes.body.data.accessToken;

  // Create sample product
  testProduct = await Product.create({
    sku: 'TEST-CEM-53',
    title: 'Test UltraTech Cement',
    description: 'High grade cement',
    brand: 'UltraTech',
    categoryId: 'cement',
    categoryName: 'Cement',
    price: 400.0,
    mrp: 500.0,
    stock: 20
  });

  // Create sample coupon (Fixed ₹200 off for orders >= ₹1000)
  await Coupon.create({
    code: 'SAVE200',
    discountType: 'FIXED_AMOUNT',
    discountValue: 200.0,
    minOrderAmount: 1000.0,
    validUntil: new Date('2028-01-01'),
    isActive: true
  });
});

describe('Cart & Server-Side Pricing Verification Tests', () => {
  it('should calculate subtotal, 18% GST and delivery fee correctly on server', async () => {
    // Add 2 bags @ ₹400 = ₹800 subtotal
    const addRes = await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 2
      });

    expect(addRes.status).toBe(200);
    const summary = addRes.body.data;

    // Subtotal: 2 * 400 = 800
    expect(summary.subtotal).toBe(800.0);
    // Subtotal < 2000 -> Delivery fee is 150
    expect(summary.deliveryFee).toBe(150.0);
    // GST 18% on 800 = 144
    expect(summary.taxAmount).toBe(144.0);
    // Grand total: 800 + 144 + 150 = 1094
    expect(summary.grandTotal).toBe(1094.0);
  });

  it('should provide free delivery when subtotal exceeds ₹2,000 threshold', async () => {
    // Add 6 bags @ ₹400 = ₹2400 (> 2000)
    const addRes = await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 6
      });

    expect(addRes.status).toBe(200);
    const summary = addRes.body.data;

    expect(summary.subtotal).toBe(2400.0);
    expect(summary.deliveryFee).toBe(0.0); // Free delivery
    expect(summary.taxAmount).toBe(432.0); // 18% of 2400
    expect(summary.grandTotal).toBe(2832.0);
  });

  it('should apply coupon and recalculate taxes on discounted taxable amount', async () => {
    // Add 3 bags = ₹1200 (> min ₹1000 for SAVE200)
    await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 3
      });

    const couponRes = await request(app)
      .post('/api/v1/cart/coupon')
      .set('Authorization', `Bearer ${authToken}`)
      .send({ couponCode: 'SAVE200' });

    expect(couponRes.status).toBe(200);
    const summary = couponRes.body.data;

    expect(summary.subtotal).toBe(1200.0);
    expect(summary.couponDiscount).toBe(200.0);
    // Taxable amount = 1200 - 200 = 1000
    // GST 18% on 1000 = 180
    expect(summary.taxAmount).toBe(180.0);
    // Delivery fee = 150 (since subtotal < 2000)
    expect(summary.deliveryFee).toBe(150.0);
    // Grand total = 1000 + 180 + 150 = 1330
    expect(summary.grandTotal).toBe(1330.0);
  });

  it('should reject adding more units than available inventory', async () => {
    const res = await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 25 // Stock is only 20
      });

    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INSUFFICIENT_STOCK');
  });
});
