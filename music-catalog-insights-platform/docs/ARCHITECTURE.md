# Architecture

```
                          ┌───────────────────────────┐
                          │        Next.js UI         │
                          │  (App Router, TypeScript) │
                          └────────────┬──────────────┘
                                       │ HTTPS (JWT bearer token)
                                       ▼
                          ┌───────────────────────────┐
                          │      Spring Boot API      │
                          │  Controller → Service →   │
                          │  Repository → Entity       │
                          └───┬───────────────────┬────┘
                              │                   │
                 ┌────────────┘                   └────────────┐
                 ▼                                              ▼
      ┌─────────────────────┐                       ┌─────────────────────┐
      │  PostgreSQL (Neon)   │                       │  External APIs      │
      │  users, saved_songs  │                       │  - iTunes Search    │
      └─────────────────────┘                       │  - Gemini (AI)      │
                                                       └─────────────────────┘
```

## Backend layers

- **Controller** – REST endpoints, request validation, HTTP status codes.
- **Service** – business logic, transactions, cache boundaries.
- **Repository** – Spring Data JPA interfaces over PostgreSQL.
- **Mapper** – converts between entities and DTOs so JPA entities never leak to the client.
- **Client** – isolates all outbound calls to iTunes and Gemini behind typed wrappers.
- **Security** – stateless JWT authentication via a custom `OncePerRequestFilter`, BCrypt password hashing, and a `@CurrentUserId` argument resolver that reads the authenticated user directly from the `SecurityContext`.

## Frontend structure

- **`app/`** – Next.js App Router pages (landing, auth, search, library, analytics, profile, 404).
- **`components/ui`** – shadcn/ui-style primitives (Button, Card, Dialog, Select, Tabs, etc.).
- **`components/songs`, `components/charts`, `components/analytics`** – feature-specific composition.
- **`lib/api`** – one Axios-based module per backend resource; a shared client attaches the JWT and normalizes errors.
- **`context/AuthContext.tsx`** – single source of truth for auth state, backed by `localStorage`.

## Data flow guarantee

The frontend has **no knowledge of `itunes.apple.com`** - `NEXT_PUBLIC_API_BASE_URL` only ever points at the Spring Boot backend. All catalog search traffic is proxied and shaped server-side.
