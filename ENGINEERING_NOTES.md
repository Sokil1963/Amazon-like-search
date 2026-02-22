# Engineering Notes

## Architecture Decisions

### Tech Stack Choice: Spring Boot + Thymeleaf + PostgreSQL

**Why Spring Boot instead of React/Node.js?**
- Server-side rendering ensures SEO-friendly pages
- Single deployable artifact (JAR)
- Strong typing with Java reduces runtime errors
- JPA/Hibernate simplifies database operations

**Trade-off:** Slower development compared to React, but better for long-term maintainability.

### Database Design

Used existing Supabase schema with:
- `products` - main product table
- `brands` - normalized brand reference
- `categories` - normalized category reference  
- `product_categories` - many-to-many junction table

**Key decision:** Native SQL queries for facet counts instead of Criteria API. This provides:
- Better performance (single query per facet type)
- Full control over GROUP BY and COUNT operations
- Easier debugging with `show-sql: true`

### Faceted Search Implementation

**Facet count calculation:**
- Brand facets filtered by selected categories (cross-filtering)
- Category facets filtered by selected brands (cross-filtering)
- Top 20 facets by product count (prevents UI overload with 2000+ categories)

**URL State:** All filters stored in URL query parameters (`?q=milk&brands=1,2&categories=5`), making results shareable.

## Scaling Considerations

### Current Limitations
- No caching (every request hits database)
- No full-text search index (using `ILIKE '%query%'`)
- Facet queries run sequentially

### How to Scale

1. **Add database indexes:**
```sql
CREATE INDEX idx_products_name_trgm ON products USING gin(name gin_trgm_ops);
CREATE INDEX idx_products_brand_id ON products(brand_id);
CREATE INDEX idx_product_categories_product ON product_categories(product_id);
CREATE INDEX idx_product_categories_category ON product_categories(category_id);
```

2. **Add Redis caching** for facet counts (they change rarely)

3. **Use Elasticsearch** for full-text search at scale (>100K products)

4. **Parallel facet queries** using `@Async` or CompletableFuture

## Non-Trivial Edge Case: Empty Filter Lists

**Problem:** Native SQL with `IN (:list)` fails when list is empty.

**Solution:** Use boolean flag pattern:
```java
List<Long> brandsForQuery = hasBrandFilter ? brands : List.of(-1L);
// In SQL:
WHERE (:hasBrandFilter = false OR b.id IN (:brandIds))
```

This ensures:
- Empty list → condition is ignored (returns all)
- Non-empty list → filters correctly
- No SQL syntax errors

**Alternative considered:** Dynamic query building with Criteria API. Rejected because it makes facet count queries complex and harder to optimize.

## Performance Characteristics

- **Page load:** ~50-100ms (with warm connection pool)
- **Facet calculation:** ~20-30ms per facet type
- **Pagination:** Efficient with Spring Data's `Pageable`

Tested with 10,000 products from Open Food Facts dataset.
