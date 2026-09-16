import request from 'supertest';
import { createApp } from '../src/app.js';
import { connectTestDB, closeTestDB, clearTestDB } from './setup.js';
import { User } from '../src/models/User.js';

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

describe('Authentication & Token API Tests', () => {
  it('should register a new customer with hashed password and return access & refresh tokens', async () => {
    const res = await request(app)
      .post('/api/v1/auth/register')
      .send({
        name: 'Amit Patel',
        email: 'amit@example.com',
        phone: '+91 98765 11223',
        password: 'securePassword123'
      });

    expect(res.status).toBe(201);
    expect(res.body.success).toBe(true);
    expect(res.body.data.accessToken).toBeDefined();
    expect(res.body.data.refreshToken).toBeDefined();
    expect(res.body.data.role).toBe('CUSTOMER');

    // Verify password is not plaintext in database
    const savedUser = await User.findOne({ email: 'amit@example.com' });
    expect(savedUser.password).not.toBe('securePassword123');
  });

  it('should reject registration with already registered email or phone', async () => {
    await request(app)
      .post('/api/v1/auth/register')
      .send({
        name: 'Amit Patel',
        email: 'amit@example.com',
        phone: '+91 98765 11223',
        password: 'securePassword123'
      });

    const duplicateRes = await request(app)
      .post('/api/v1/auth/register')
      .send({
        name: 'Amit Duplicate',
        email: 'amit@example.com',
        phone: '+91 98765 99999',
        password: 'securePassword123'
      });

    expect(duplicateRes.status).toBe(400);
    expect(duplicateRes.body.success).toBe(false);
    expect(duplicateRes.body.code).toBe('USER_ALREADY_EXISTS');
  });

  it('should login with valid credentials and reject invalid password', async () => {
    await request(app)
      .post('/api/v1/auth/register')
      .send({
        name: 'Vikram Singh',
        email: 'vikram@example.com',
        phone: '+91 98111 22334',
        password: 'myPassword123'
      });

    // Valid login
    const loginRes = await request(app)
      .post('/api/v1/auth/login')
      .send({
        emailOrPhone: 'vikram@example.com',
        password: 'myPassword123'
      });

    expect(loginRes.status).toBe(200);
    expect(loginRes.body.data.accessToken).toBeDefined();

    // Invalid login
    const wrongLogin = await request(app)
      .post('/api/v1/auth/login')
      .send({
        emailOrPhone: 'vikram@example.com',
        password: 'wrongPassword'
      });

    expect(wrongLogin.status).toBe(401);
    expect(wrongLogin.body.code).toBe('INVALID_CREDENTIALS');
  });

  it('should rotate refresh token and issue new access token', async () => {
    const regRes = await request(app)
      .post('/api/v1/auth/register')
      .send({
        name: 'Pooja Roy',
        email: 'pooja@example.com',
        phone: '+91 98222 33445',
        password: 'password123'
      });

    const oldRefreshToken = regRes.body.data.refreshToken;

    const refreshRes = await request(app)
      .post('/api/v1/auth/refresh-token')
      .send({ refreshToken: oldRefreshToken });

    expect(refreshRes.status).toBe(200);
    expect(refreshRes.body.data.accessToken).toBeDefined();
    expect(refreshRes.body.data.refreshToken).toBeDefined();
    expect(refreshRes.body.data.refreshToken).not.toBe(oldRefreshToken);

    // Old refresh token must now be invalid
    const reuseRes = await request(app)
      .post('/api/v1/auth/refresh-token')
      .send({ refreshToken: oldRefreshToken });

    expect(reuseRes.status).toBe(401);
  });
});
