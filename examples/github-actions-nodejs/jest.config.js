module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  coverageDirectory: 'coverage',
  reporters: [
    'default',
    ['jest-junit', { outputDirectory: '.', outputName: 'jest-junit.xml' }]
  ]
};
