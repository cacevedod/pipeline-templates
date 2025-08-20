import app from '../src/app';
import supertest from 'supertest';

describe('GET /', () => {
  it('debe responder con mensaje de bienvenida', async () => {
    const response = await supertest(app.server).get('/');
    expect(response.status).toBe(200);
    expect(response.body).toEqual({ message: '¡Hola mundo desde Fastify + TypeScript!' });
  });
});
