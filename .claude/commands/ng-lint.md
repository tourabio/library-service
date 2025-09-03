---
allowed-tools: Bash(npm run lint), Bash(npm run lint:fix), Edit(src/**)
description: Run ESLint validation and auto-fix issues
---
Execute comprehensive linting for Angular codebase:

LINTING PROCESS:
1. Run ESLint: `npm run lint`
2. Attempt auto-fix: `npm run lint:fix`
3. Report remaining issues
4. Provide fix recommendations

VALIDATION RULES:
- TypeScript strict mode compliance
- No `any` types allowed
- Explicit return type annotations
- Proper Angular lifecycle usage
- RxJS best practices
- Accessibility requirements

FIX PRIORITY:
1. **Critical**: TypeScript errors, security issues
2. **High**: Angular anti-patterns, performance issues
3. **Medium**: Style consistency, naming conventions
4. **Low**: Formatting, minor optimizations

OUTPUT FORMAT:
- 🔴 Critical issues requiring immediate attention
- 🟡 Warnings that should be addressed
- ✅ Auto-fixed issues
- 📋 Summary statistics