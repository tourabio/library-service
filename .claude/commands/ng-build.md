---
allowed-tools: Bash(ng build), Bash(ng build --configuration production), Read(dist/**), Read(src/environments/**), Edit(src/environments/**)
argument-hint: [environment]
description: Build Angular application with Quarkus backend integration and optimization
---
Build Angular application for specified environment: $ARGUMENTS

BACKEND INTEGRATION:
- Framework: Quarkus 3.24.5 backend
- API Base URL: /api (configured in environments)
- Development: http://localhost:8080/api
- Production: Configure for deployed backend URL

BUILD PROCESS:
1. Validate environment configuration
2. Check API endpoint alignment
3. Clean previous builds
4. Run production build: `ng build --configuration ${ARGUMENTS:-production}`
5. Analyze bundle sizes
6. Validate backend connectivity

ENVIRONMENT VALIDATION:
- Development: Points to local Quarkus (http://localhost:8080/api)
- Staging: Points to staging backend
- Production: Points to production backend
- API paths match backend /api base path

OPTIMIZATION CHECKS:
- Tree shaking effectiveness
- Bundle size within limits (<500KB initial)
- Lazy loading implementation
- Source map generation (staging only)
- Service worker configuration
- Angular Material bundle optimization

PERFORMANCE VALIDATION:
- Initial bundle size: <500KB
- Feature bundles: <200KB each
- Angular Material chunks optimized
- HTTP client with proper error handling
- Asset compression enabled

BACKEND COMPATIBILITY:
- API service endpoints match Quarkus routes
- Transfer objects align with backend DTOs
- Error handling for backend exceptions
- CORS configuration validated

QUALITY GATES:
- Zero build errors
- Bundle size within budget
- All routes properly lazy loaded
- No unused dependencies
- Environment URLs correctly configured
- Backend API alignment verified

REPORTING:
- 📦 Bundle size breakdown
- ⚡ Performance metrics
- 🎯 Optimization opportunities
- 📊 Build statistics
- 🔗 Backend integration status