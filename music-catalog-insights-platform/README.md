# 🎵 Music Catalog Insights Platform

An end-to-end full-stack application where users search the iTunes catalog, build a personal
saved-song library, explore a six-chart analytics dashboard, and generate AI-powered "Music Taste
Insights" from their own library.

Built for the *Music Catalog Insights Platform* take-home assignment.

---

## Table of contents

1. [Project overview](#project-overview)
2. [Architecture](#architecture)
3. [Tech stack](#tech-stack)
4. [Folder structure](#folder-structure)
5. [Entity choice](#entity-choice)
6. [Database schema](#database-schema)
7. [API documentation](#api-documentation)
8. [Authentication flow](#authentication-flow)
9. [AI feature](#ai-feature)
10. [Analytics](#analytics)
11. [Getting started (local development)](#getting-started-local-development)
12. [Deployment guide](#deployment-guide)
13. [Environment variables](#environment-variables)
14. [Trade-offs](#trade-offs)
15. [Future improvements](#future-improvements)

---

## Project overview

Music streaming platforms need a way to let users build and explore a personal music library,
sourced from a public catalog, with analytics and AI-driven insights layered on top. This project
implements exactly that:

- **Register / Login** with JWT-based authentication.
- **Search** songs via the public iTunes Search API — proxied entirely through the backend.
- **Save** songs into a private, per-user library (with duplicate protection).
- **Edit** a song's personal rating and notes, or **delete** it.
- **Analytics dashboard** with 6 Recharts visualizations and 6 summary cards.
- **AI Insights** — a Gemini-powered "Music Taste Insights" feature that analyzes *only* the
  user's saved library and returns a structured taste profile plus 5 recommendations.

---

## Architecture

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for a full diagram and layer-by-layer
breakdown. In short:

```
Next.js (client) → Spring Boot REST API → PostgreSQL
                                        → iTunes Search API (search proxy)
                                        → Gemini API (AI insights)
```

The frontend **never** calls `itunes.apple.com` or the Gemini API directly. Every external call is
made by the Spring Boot backend, which is the only component holding any third-party credentials.

---

## Tech stack

**Backend**
- Java 21, Spring Boot 3.3
- Spring Security + JWT (`jjwt`)
- Spring Data JPA / Hibernate
- Maven
- springdoc-openapi (Swagger UI)
- Spring Cache (in-memory) for search & analytics caching

**Frontend**
- Next.js 14 (App Router), React 18, TypeScript
- TailwindCSS + shadcn/ui-style primitives (Radix UI under the hood)
- Axios with a JWT request interceptor and centralized error handling
- Recharts for all data visualizations
- `sonner` for toast notifications, `use-debounce` pattern for search

**Database**
- PostgreSQL (designed for Neon in production; H2 in-memory for tests)

**AI**
- Google Gemini (`gemini-1.5-flash`) via a thin `GeminiClient` wrapper. Groq can be swapped in by
  replacing `GeminiClient` with an equivalent client, since the rest of the pipeline (prompt
  construction, JSON parsing, DTO shape) is provider-agnostic.

---

## Folder structure

```
music-catalog-insights-platform/
├── backend/                     # Spring Boot application
│   ├── src/main/java/com/musiccatalog/
│   │   ├── client/              # ItunesClient, GeminiClient
│   │   ├── config/               # Security, CORS, Cache, OpenAPI, WebClient config
│   │   ├── controller/           # REST controllers
│   │   ├── dto/                  # request/ and response/ DTOs
│   │   ├── entity/                # User, SavedSong JPA entities
│   │   ├── exception/             # Custom exceptions + GlobalExceptionHandler
│   │   ├── mapper/                # Entity <-> DTO mappers
│   │   ├── repository/            # Spring Data JPA repositories
│   │   ├── security/               # JWT filter/service, CurrentUserId resolver
│   │   └── service/                # Business logic
│   ├── src/main/resources/        # application*.yml
│   ├── src/test/java/...          # Unit tests
│   ├── Dockerfile
│   ├── pom.xml
│   └── .env.example
├── frontend/                     # Next.js application
│   ├── src/app/                   # Landing, login, register, search, library,
│   │                               # analytics, profile, 404 pages
│   ├── src/components/            # ui/, layout/, songs/, charts/, analytics/, common/
│   ├── src/context/AuthContext.tsx
│   ├── src/lib/                   # api/, hooks/, types/, utils/
│   ├── Dockerfile
│   ├── package.json
│   └── .env.example
├── database/
│   ├── schema.sql                 # Reference DDL (also auto-managed by Hibernate)
│   └── seed.sql                   # Optional demo data
├── docs/
│   ├── API.md
│   └── ARCHITECTURE.md
├── postman/
│   └── Music-Catalog-Insights.postman_collection.json
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Entity choice

**Chosen entity: Songs**

Songs were chosen over Albums or Artists because they provide the richest, most granular set of
attributes for analytics: `duration`, `genre`, `releaseDate`, and `artistName` are all present on
every individual track returned by `entity=song` from the iTunes Search API. This granularity
directly enables all 6 required visualizations (genre distribution, top artists, ratings, release
year distribution, average rating by genre, and growth over time) without needing a second lookup
call per album/artist. Albums would only provide `trackCount` and a single release date per
collection, losing per-song rating/notes granularity; Artists provide almost no structured
metadata at all from a plain search call.

---

## Database schema

PostgreSQL, two tables, one `user_id` foreign key. See [`database/schema.sql`](database/schema.sql)
for full DDL (also reproduced by Hibernate at boot via `ddl-auto`).

```
users                              saved_songs
─────────────────                  ──────────────────────────
id               BIGSERIAL PK      id                 BIGSERIAL PK
name             VARCHAR(120)      apple_catalog_id    BIGINT
email            VARCHAR(180) UQ   title               VARCHAR(255)
password         VARCHAR(255)      artist_name         VARCHAR(255)
created_at       TIMESTAMPTZ       genre               VARCHAR(120)
updated_at       TIMESTAMPTZ       release_date        DATE
                                   duration_millis     INTEGER
                                   artwork_url         VARCHAR(500)
                                   user_rating         INTEGER (1-5)
                                   user_notes          VARCHAR(2000)
                                   created_at          TIMESTAMPTZ
                                   updated_at          TIMESTAMPTZ
                                   user_id             BIGINT FK -> users.id

                                   UNIQUE (user_id, apple_catalog_id)  -- prevents duplicates
```

**Why SQL / PostgreSQL:** the data is inherently relational (one user has many saved songs), the
schema is stable and well-defined up front, and analytics require aggregate SQL-friendly
operations (`GROUP BY` genre/artist/year, averages). A relational database with a simple
unique constraint is also the most reliable way to enforce "no duplicate song per user" at the
data layer, rather than relying purely on application logic.

---

## API documentation

Full endpoint list, request/response shapes, and error format: [`docs/API.md`](docs/API.md).

Interactive Swagger UI (when the backend is running): `http://localhost:8080/swagger-ui.html`

Postman collection: [`postman/Music-Catalog-Insights.postman_collection.json`](postman/Music-Catalog-Insights.postman_collection.json)
(auto-captures the JWT from register/login into a collection variable for subsequent requests).

| Method | Path               | Description                                  |
|--------|--------------------|-----------------------------------------------|
| POST   | /api/auth/register | Register a new user                           |
| POST   | /api/auth/login    | Log in, returns a JWT                         |
| GET    | /api/search        | Proxy search to iTunes (`?query=...&type=song`) |
| GET    | /api/library        | Paginated list of saved songs                 |
| GET    | /api/library/{id}   | Fetch a single saved song                     |
| POST   | /api/library        | Save a song to the library                    |
| PUT    | /api/library/{id}   | Update rating/notes                           |
| DELETE | /api/library/{id}   | Remove a song                                 |
| GET    | /api/analytics      | Aggregated analytics for the dashboard        |
| POST   | /api/ai/insights    | Generate AI Music Taste Insights              |

---

## Authentication flow

1. **Register** (`POST /api/auth/register`) — password is hashed with BCrypt (strength 12) before
   being persisted; a JWT is returned immediately so the user is logged in on sign-up.
2. **Login** (`POST /api/auth/login`) — credentials are verified against the stored BCrypt hash; a
   fresh JWT is issued.
3. The frontend stores the JWT in `localStorage` and an Axios request interceptor
   (`src/lib/api/client.ts`) attaches it as `Authorization: Bearer <token>` on every request.
4. On the backend, a custom `JwtAuthenticationFilter` (a `OncePerRequestFilter`) validates the
   token on every request and populates the `SecurityContext`.
5. A custom `@CurrentUserId` parameter annotation + `HandlerMethodArgumentResolver` lets
   controllers request the authenticated user's id directly, without ever trusting a
   client-supplied user id — every library/analytics/AI query is scoped to
   `SecurityContextHolder`'s authenticated principal.
6. A response interceptor on the frontend clears the stored token and redirects to `/login` on any
   `401 Unauthorized` response.

---

## AI feature

**Music Taste Insights** (the one production AI feature required by the assignment).

- Endpoint: `POST /api/ai/insights` (JWT protected).
- `AiInsightsService` loads *only* the authenticated user's saved songs, serializes them
  (title/artist/genre/releaseDate/rating) into a prompt, and instructs Gemini to return a single
  strict JSON object — no markdown fences, no extra commentary — matching the exact shape of
  `AiInsightsResponse`.
- The prompt explicitly instructs the model **not to invent facts about songs that are in the
  library** and to base every field (favourite genres/artists, mood, era preference, personalized
  suggestions) strictly on the supplied data. The 5 song recommendations are the one place new
  songs are intentionally generated (that's the point of a recommendation feature), and the prompt
  requires them to be different from what's already saved.
- A minimum library size (3 songs) is enforced before calling the AI, both to give the model enough
  signal and to avoid wasting API calls on empty libraries.
- Response parsing failures or missing API keys raise a typed `AiServiceException`, mapped to
  `503 Service Unavailable` by the global exception handler rather than leaking a stack trace.

---

## Analytics

`GET /api/analytics` returns everything the dashboard needs in one call (cached per-user in
memory via Spring Cache, invalidated implicitly whenever the library changes on the next
request cycle):

**Summary cards:** Total Songs · Average Rating · Unique Artists · Unique Genres · Highest Rated
Song · Latest Added Song.

**Charts (Recharts, 6 required, 6 implemented):**
1. **Genre Distribution** — Pie/Donut chart.
2. **Top Artists** — Horizontal bar chart (top 10).
3. **Songs Added Over Time** — Line chart, grouped by day.
4. **Ratings Distribution** — Histogram (bar chart across 1–5 stars).
5. **Release Year Distribution** — Bar chart across release years.
6. **Average Rating by Genre** — Bar chart of mean rating per genre.

All aggregation happens server-side in `AnalyticsService` using plain Java streams over the
user's saved songs, so the frontend only ever renders pre-shaped data.

---

## Getting started (local development)

### Prerequisites
- Java 21, Maven 3.9+
- Node.js 20+
- PostgreSQL 16 (or Docker)
- A Gemini API key (optional locally — AI insights will return a clear error without one)

### 1. Database
```bash
# Option A: Docker
docker run --name mci-postgres -e POSTGRES_DB=music_catalog \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16-alpine

# Option B: apply database/schema.sql to an existing PostgreSQL instance
psql -U postgres -d music_catalog -f database/schema.sql
```

### 2. Backend
```bash
cd backend
cp .env.example .env   # then edit values as needed
# Export the variables in .env into your shell, or configure them in your IDE run config
mvn spring-boot:run
```
The API will be available at `http://localhost:8080/api`, Swagger UI at
`http://localhost:8080/swagger-ui.html`.

### 3. Frontend
```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```
The app will be available at `http://localhost:3000`.

### 4. Or run everything with Docker Compose
```bash
GEMINI_API_KEY=your-key-here docker compose up --build
```

---

## Deployment guide

| Layer     | Target             | Notes                                                                 |
|-----------|---------------------|------------------------------------------------------------------------|
| Frontend  | **Vercel**           | Set `NEXT_PUBLIC_API_BASE_URL` to your deployed Render backend URL.    |
| Backend   | **Render**           | Deploy `backend/` as a Docker service (uses the provided `Dockerfile`). Set all backend env vars from `.env.example` in the Render dashboard. |
| Database  | **Neon PostgreSQL**  | Create a Neon project, copy the pooled connection string into `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. |

**Steps:**
1. Provision a Neon PostgreSQL database and run `database/schema.sql` against it (or let
   `ddl-auto=update` create it on first boot).
2. Deploy `backend/` to Render as a Docker web service; set `SPRING_PROFILES_ACTIVE=prod` and all
   variables listed below. Render provides `PORT` automatically, which `application.yml` already
   reads.
3. Deploy `frontend/` to Vercel; set `NEXT_PUBLIC_API_BASE_URL=https://<your-render-service>.onrender.com/api`.
4. Update the backend's `CORS_ALLOWED_ORIGINS` to include your Vercel domain.

---

## Environment variables

**Backend** (`backend/.env.example`):
```
PORT=8080
SPRING_PROFILES_ACTIVE=dev
DB_URL=jdbc:postgresql://localhost:5432/music_catalog
DB_USERNAME=postgres
DB_PASSWORD=postgres
DDL_AUTO=update
JWT_SECRET=change-this-to-a-long-random-secret-value-in-production-min-32-chars
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:3000
ITUNES_API_BASE_URL=https://itunes.apple.com
GEMINI_BASE_URL=https://generativelanguage.googleapis.com
GEMINI_API_KEY=
GEMINI_MODEL=gemini-1.5-flash
```

**Frontend** (`frontend/.env.example`):
```
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

---

## Trade-offs

- **In-memory Spring Cache** (`ConcurrentMapCacheManager`) is used instead of Redis for search and
  analytics caching. This is appropriate for a single-instance take-home deployment but would need
  to move to a distributed cache (Redis) for multi-instance production deployments.
- **JWT is stored in `localStorage`** rather than an httpOnly cookie, trading a small XSS-related
  risk surface for significant implementation simplicity (no CSRF token plumbing, works cleanly
  across a separately-deployed frontend and backend on different domains).
- **Analytics are recomputed from the full saved-song list on each cache miss** rather than
  maintained incrementally. For the expected library sizes of a personal music catalog (tens to
  low thousands of songs) this is fast and far simpler than maintaining running aggregates.
- **Gemini is the only wired AI provider.** The `GeminiClient` is isolated behind a single
  interface-shaped class specifically so swapping in Groq (or any other JSON-capable LLM API)
  is a localized change, but only one provider is implemented to keep the surface area focused.
- **Duplicate prevention is enforced at the database level** (`UNIQUE(user_id, apple_catalog_id)`)
  in addition to an application-level check, favoring data integrity over saving one query.

---

## Future improvements

- Redis-backed distributed caching and rate limiting for the search proxy.
- Refresh tokens / token rotation instead of a single long-lived JWT.
- Bulk "save all results" from a search page.
- Export library / analytics as CSV or PDF.
- Optimistic UI updates for rating/notes edits.
- Integration tests against Testcontainers-backed PostgreSQL (unit tests currently cover the
  service layer; H2 is wired for a full Spring context test but not yet exercised end-to-end).
- Social features (shareable, read-only analytics snapshots).

Build by Tejas ! 🪽