---
allowed-tools: Bash(npm run test), Bash(npm run test:coverage), Read(src/**/*.spec.ts)
description: Run comprehensive Angular tests with coverage analysis
---
Execute complete testing suite for Angular library management system:

TESTING EXECUTION:
1. Run all unit tests: `npm run test`
2. Generate coverage report: `npm run test:coverage`
3. Analyze coverage results
4. Identify untested code paths
5. Report coverage statistics

COVERAGE REQUIREMENTS:
- Overall coverage: >80%
- Component coverage: >85%
- Service coverage: >90%
- Pipe/Directive coverage: >95%

QUALITY GATES:
- All tests must pass
- No skipped tests allowed
- Coverage thresholds must be met
- No console errors during test execution

REPORTING FORMAT:
- ✅ Total tests passed/failed
- 📊 Coverage percentages by type
- 🎯 Files below coverage threshold
- 📝 Recommendations for improvement