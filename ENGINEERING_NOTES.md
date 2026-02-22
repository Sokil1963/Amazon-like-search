# Engineering Notes

Project: Faceted product search (text search + brand/category facets). This document briefly explains the main engineering decisions made for the take‑home assignment.

Key tradeoffs
- Simplicity & delivery speed over full-text search accuracy: used PostgreSQL ILIKE for partial matches (simple, works on small datasets) instead of introducing Elastic/Algolia. This keeps the stack small and deployable to a free Supabase Postgres instance, but is less performant for large datasets and less relevant than an inverted-index search.
- Server-side rendering and Spring Boot (Java) vs single-page app: chose Spring Boot + Thymeleaf to provide a single deployable artifact and SEO-friendly pages. This reduces frontend complexity and dependency surface, but results in less interactive UX than a React client and slightly slower perceived UI updates.
- Native SQL for facet counts vs dynamic ORM queries: wrote focused SQL queries for facet counts to ensure predictable performance and ability to optimize GROUP BY/COUNT. This sacrifices some ORM portability and requires careful parameter handling.

How I would scale this further
- Improve search quality and scale: introduce Elasticsearch or a hosted search service (Algolia) for fast, relevance-scored partial/full-text queries and typo tolerance.
- Add caching layer: cache facet counts and popular search results in Redis with short TTLs; invalidate on product updates. This reduces DB load for high read traffic.
- Add DB-level improvements: trigram index (pg_trgm) on product.name for ILIKE performance and indexes on product->brand/category foreign keys and junction table.
- Parallelize facet queries: compute independent facet counts concurrently (CompletableFuture/@Async) to reduce tail latency.
- Horizontal scaling: stateless app instances behind a load balancer, shared Redis and managed Postgres (or read replicas) for high availability.

Non-trivial edge case / technical decision
- Empty filter lists in SQL IN clauses: SQL with IN (:list) fails or produces incorrect logic when the list is empty. To handle this robustly I used the boolean-flag pattern in queries:
  - on the DB side: WHERE (:hasFilter = false OR col IN (:ids))
  - on the service side: pass a hasFilter boolean and either the ids list or a safe placeholder.
This ensures that an empty selection behaves like "no filter" (returns all) while a populated list applies the filter. It avoids dynamic SQL string building and prevents syntax errors.

Other concise notes
- URL state: all search state (q, brands, categories, page) is encoded in query parameters so results are shareable/bookmarkable.
- UX limits: facet lists are capped (top N by count) to avoid overwhelming the UI when category/brand cardinality is very large.

Requirements coverage
- Search: partial matches via ILIKE, paginated results implemented.
- Facets: Brand and Category multi-select implemented; counts update based on active filters.
- URL State: search and filters reflected in the URL.

If you want, I can shorten this further to a single paragraph, add exact SQL examples used, or produce a short README snippet explaining how to deploy the app to Vercel/Netlify + Supabase.
