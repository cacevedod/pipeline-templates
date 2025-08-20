module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  coverageDirectory: 'coverage',
  reporters: [
    'default',
      [
        'jest-junit',
        {
          outputDirectory: process.env.JEST_JUNIT_OUTPUT_DIR || '.',
          outputName: process.env.JEST_JUNIT_OUTPUT_NAME || 'jest-junit.xml',
        },
      ],
  ]
};
