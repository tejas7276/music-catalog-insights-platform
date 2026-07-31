# API Reference

Full interactive documentation is available via Swagger/OpenAPI once the backend is running:

```
http://localhost:8080/swagger-ui.html
```

A ready-to-import Postman collection is also provided at `/postman/Music-Catalog-Insights.postman_collection.json`.

## Conventions

- All request/response bodies are JSON.
- All endpoints except `/api/auth/register` and `/api/auth/login` require a `Authorization: Bearer <token>` header.
- Errors follow a consistent shape:

```json
{
  "timestamp": "2026-07-31T10:15:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Song not found in your library: id=42",
  "path": "/api/library/42",
  "details": null
}
```

## Endpoints

| Method | Path                  | Auth | Description                                      |
|--------|-----------------------|------|---------------------------------------------------|
| POST   | /api/auth/register    | No   | Create a new account, returns a JWT               |
| POST   | /api/auth/login       | No   | Authenticate, returns a JWT                        |
| GET    | /api/search           | Yes  | Proxy a song search to the iTunes Search API       |
| GET    | /api/library          | Yes  | Paginated list of the user's saved songs           |
| GET    | /api/library/{id}     | Yes  | Get a single saved song                            |
| POST   | /api/library          | Yes  | Save a song to the library (duplicate-safe)        |
| PUT    | /api/library/{id}     | Yes  | Update rating/notes for a saved song                |
| DELETE | /api/library/{id}     | Yes  | Remove a song from the library                     |
| GET    | /api/analytics        | Yes  | Aggregated analytics for the dashboard             |
| POST   | /api/ai/insights      | Yes  | Generate AI-powered Music Taste Insights           |
