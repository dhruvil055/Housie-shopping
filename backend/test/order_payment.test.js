import request from 'supertest';
import crypto from 'crypto';
import { createApp } from '../src/app.js';
import { connectTestDB, closeTestDB, clearTestDB } from './setup.js';
import { Product } from '../src/models/Product.js';
import { Order } from '../src/models/Order.js';

const app = createApp();
let authToken;
let testProduct;

beforeAll(async () => {
  await connectTestDB();
  process.env.RAZORPAY_KEY_SECRET = 'test_razorpay_secret_key';
});

afterAll(async () => {
  await closeTestDB();
});

beforeEach(async () => {
  await clearTestDB();

  const regRes = await request(app)
    .post('/api/v1/auth/register')
    .send({
      name: 'Order Tester',
      email: 'order@example.com',
      phone: '+91 95555 66666',
      password: 'password123'
    });
  authToken = regRes.body.data.accessToken;

  testProduct = await Product.create({
    sku: 'ORD-TEST-53',
    title: 'Tata Steel 12mm',
    description: 'High tensile strength',
    brand: 'Tata',
    categoryId: 'steel',
    categoryName: 'Steel',
    price: 500.0,
    mrp: 600.0,
    stock: 10,
    reservedStock: 0
  });
});

describe('Checkout, Orders & Payment Signature Verification Tests', () => {
  it('should create an order, reserve stock, and return payment order details', async () => {
    // Add 2 units to cart
    await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 2
      });

    // Checkout
    const checkoutRes = await request(app)
      .post('/api/v1/orders/create')
      .set('Authorization', `Bearer ${authToken}`)
      .set('X-Idempotency-Key', 'idemp_key_1001')
      .send({
        address: {
          fullName: 'Order Tester',
          phone: '+91 95555 66666',
          houseFlat: 'Flat 101',
          city: 'Gurugram',
          state: 'Haryana',
          postalCode: '122001'
        },
        paymentMethod: 'Razorpay / UPI / Card'
      });

    expect(checkoutRes.status).toBe(201);
    expect(checkoutRes.body.success).toBe(true);
    expect(checkoutRes.body.data.orderId).toBeDefined();
    expect(checkoutRes.body.data.razorpayOrderId).toBeDefined();

    // Verify stock is reserved in database
    const updatedProduct = await Product.findById(testProduct._id);
    expect(updatedProduct.reservedStock).toBe(2);
  });

  it('should verify valid Razorpay cryptographic signature and mark order as PAID', async () => {
    // Add to cart and checkout
    await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 1
      });

    const checkoutRes = await request(app)
      .post('/api/v1/orders/create')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        address: {
          fullName: 'Order Tester',
          phone: '+91 95555 66666',
          houseFlat: 'Flat 101',
          city: 'Gurugram',
          state: 'Haryana',
          postalCode: '122001'
        },
        paymentMethod: 'Razorpay / UPI / Card'
      });

    const orderId = checkoutRes.body.data.orderId;
    const razorpayOrderId = checkoutRes.body.data.razorpayOrderId;
    const razorpayPaymentId = 'pay_test_payment_999';

    // Generate valid HMAC SHA-256 signature
    const validSignature = crypto
      .createHmac('sha256', 'test_razorpay_secret_key')
      .update(`${razorpayOrderId}|${razorpayPaymentId}`)
      .digest('hex');

    const verifyRes = await request(app)
      .post('/api/v1/orders/verify-payment')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        orderId,
        razorpayOrderId,
        razorpayPaymentId,
        razorpaySignature: validSignature
      });

    expect(verifyRes.status).toBe(200);
    expect(verifyRes.body.data.paymentStatus).toBe('PAID');
    expect(verifyRes.body.data.orderStatus).toBe('CONFIRMED');

    // Verify stock was permanently deducted and reservation released
    const finalProduct = await Product.findById(testProduct._id);
    expect(finalProduct.stock).toBe(9); // 10 - 1
    expect(finalProduct.reservedStock).toBe(0);
  });

  it('should reject tampered payment signature and release reserved stock', async () => {
    await request(app)
      .post('/api/v1/cart/items')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        productId: testProduct._id.toString(),
        quantity: 1
      });

    const checkoutRes = await request(app)
      .post('/api/v1/orders/create')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        address: {
          fullName: 'Order Tester',
          phone: '+91 95555 66666',
          houseFlat: 'Flat 101',
          city: 'Gurugram',
          state: 'Haryana',
          postalCode: '122001'
        },
        paymentMethod: 'Razorpay / UPI / Card'
      });

    const orderId = checkoutRes.body.data.orderId;
    const razorpayOrderId = checkoutRes.body.data.razorpayOrderId;

    const verifyRes = await request(app)
      .post('/api/v1/orders/verify-payment')
      .set('Authorization', `Bearer ${authToken}`)
      .send({
        orderId,
        razorpayOrderId,
        razorpayPaymentId: 'pay_tampered',
        razorpaySignature: 'fake_tampered_signature_hex'
      });

    expect(verifyRes.status).toBe(400);
    expect(verifyRes.body.code).toBe('PAYMENT_VERIFICATION_FAILED');

    // Reserved stock must be restored
    const finalProduct = await Product.findById(testProduct._id);
    expect(finalProduct.stock).toBe(10);
    expect(finalProduct.reservedStock).toBe(0);
  });
});
