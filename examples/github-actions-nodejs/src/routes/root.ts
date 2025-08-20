import { FastifyInstance, FastifyPluginOptions } from 'fastify';

export default function rootRoute(app: FastifyInstance, opts: FastifyPluginOptions, done: () => void) {
  app.get('/', async (request, reply) => {
    return { message: '¡Hola mundo desde Fastify + TypeScript!' };
  });
  done();
}
