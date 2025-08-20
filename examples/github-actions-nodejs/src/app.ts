import Fastify from 'fastify';
import rootRoute from './routes/root';

const app = Fastify();

app.register(rootRoute);

const start = async () => {
  try {
    await app.listen({ port: 3000, host: '0.0.0.0' });
    console.log('Servidor iniciado en http://localhost:3000');
  } catch (err) {
    app.log.error(err);
    process.exit(1);
  }
};


if (require.main === module) {
  start();
}

export { start };

export default app;
