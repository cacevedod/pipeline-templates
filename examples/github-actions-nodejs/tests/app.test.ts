
import app from '../src/app';

describe('GET /', () => {
  it('debe responder con mensaje de bienvenida', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/'
    });
    expect(response.statusCode).toBe(200);
    expect(response.json()).toEqual({ message: '¡Hola mundo desde Fastify + TypeScript!' });
  });
});
