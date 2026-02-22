# Faceted Product Search

Amazon-like faceted search for a product catalog built with Spring Boot.

## Live Demo

🔗 [https://your-app.railway.app](https://your-app.railway.app) *(update after deployment)*

## Features

- ✅ **Text search** with partial matches
- ✅ **Brand facet** (multi-select) with dynamic counts
- ✅ **Category facet** (multi-select) with dynamic counts
- ✅ **Cross-filtering** - facet counts update based on other filters
- ✅ **URL state** - shareable search results
- ✅ **Pagination** - 20 products per page
- ✅ **Product images** from Open Food Facts

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.4
- **Frontend:** Thymeleaf, vanilla CSS
- **Database:** PostgreSQL (Supabase)
- **Hosting:** Railway

## Local Development

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL database (or Supabase account)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/YOUR_USERNAME/faceted-search.git
cd faceted-search
```

2. Configure database in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://YOUR_HOST:5432/postgres
    username: YOUR_USER
    password: YOUR_PASSWORD
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Open http://localhost:8090

## Database Setup

See [faceted-search-database/README.md](faceted-search-database/README.md) for database schema and seed instructions.

## Project Structure

```
src/main/java/mdek/
├── FacetedSearchApplication.java    # Entry point
├── controller/
│   └── SearchController.java        # Main search endpoint
├── service/
│   └── ProductService.java          # Business logic
├── repository/
│   ├── ProductRepository.java       # Product queries
│   ├── BrandRepository.java         # Brand facet counts
│   └── CategoryRepository.java      # Category facet counts
├── entity/
│   ├── Product.java
│   ├── Brand.java
│   └── Category.java
└── dto/
    ├── SearchResult.java
    └── FacetCount.java
```

## Engineering Decisions

See [ENGINEERING_NOTES.md](ENGINEERING_NOTES.md) for detailed technical decisions and trade-offs.
