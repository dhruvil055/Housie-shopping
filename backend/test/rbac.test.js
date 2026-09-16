import request from 'supertest';
import { createApp } from '../src/app.js';
import { connectTestDB, closeTestDB, clearTestDB } from './setup.js';
import { User } from '../src/models/User.js';
import { ROLES } from '../src/config/constants.js';

const app = createApp();
let customerToken;
let adminToken;

beforeAll(async () => {
  await connectTestDB();
});

afterAll(async () => {
  await closeTestDB();
});

beforeEach(async () => {
  await clearTestDB();

  // Create Customer User
  const customer = new User({
    name: 'Customer User',
    email: 'customer@example.com',
    phone: '+91 91111 22222',
    password: 'password123',
    role: ROLES.CUSTOMER
  });
  customerToken = customer.generateAccessToken();
  await customer.save();

  // Create Admin User
  const admin = new User({
    name: 'Store Admin',
    email: 'admin@housieshopping.com',
    phone: '+91 99999 88888',
    password: 'password123',
    role: ROLES.SUPER_ADMIN
  });
  adminToken = admin.generateAccessToken();
  await admin.save();
});

describe('Role-Based Access Control (RBAC) Security Tests', () => {
  it('should deny customer access to admin analytics endpoint with 403 Forbidden', async () => {
    const res = await request(app)
      .get('/api/v1/admin/analytics')
      .set('Authorization', `Bearer ${customerToken}`);

    expect(res.status).toBe(403);
    expect(res.body.success).toBe(false);
    expect(res.body.code).toBe('INSUFFICIENT_PERMISSIONS');
  });

  it('should deny customer from creating new products via admin API', async () => {
    const res = await request(app)
      .post('/api/v1/admin/products')
      .set('Authorization', `Bearer ${customerToken}`)
      .send({
        sku: 'HACK-01',
        title: 'Unauthorized Product',
        brand: 'Fake',
        price: 10,
        mrp: 20,
        stock: 100
      });

    expect(res.status).toBe(403);
  });

  it('should allow authorized admin to access analytics dashboard', async () => {
    const res = await request(app)
      .get('/api/v1/admin/analytics')
      .set('Authorization', `Bearer ${adminToken}`);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.totalOrders).toBeDefined();
  });
});
