import app, { start } from '../src/app';

describe('start()', () => {
  let listenSpy: jest.SpyInstance;
  let logErrorSpy: jest.SpyInstance;
  let processExitSpy: jest.SpyInstance;

  beforeEach(() => {
    listenSpy = jest.spyOn(app, 'listen').mockImplementation(async () => undefined as any);
    logErrorSpy = jest.spyOn(app.log, 'error').mockImplementation(() => {});
    processExitSpy = jest.spyOn(process, 'exit').mockImplementation((() => undefined) as any);
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('inicia el servidor correctamente', async () => {
    await start();
    expect(listenSpy).toHaveBeenCalledWith({ port: 3000, host: '0.0.0.0' });
  });

  it('maneja errores al iniciar el servidor', async () => {
    const error = new Error('Fallo');
    listenSpy.mockRejectedValueOnce(error);
    await start();
    expect(logErrorSpy).toHaveBeenCalledWith(error);
    expect(processExitSpy).toHaveBeenCalledWith(1);
  });
});
