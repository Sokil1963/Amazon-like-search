package mdek.repository;

import jakarta.persistence.criteria.JoinType;
import mdek.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Спецификации для динамических запросов продуктов.
 * Использует Criteria API — чистый и типобезопасный способ.
 */
public class ProductSpecification {

    /**
     * Поиск по имени (регистронезависимый)
     */
    public static Specification<Product> nameContains(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        return (root, _, cb) -> cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
    }

    /**
     * Фильтр по брендам
     */
    public static Specification<Product> brandIn(List<Long> brandIds) {
        if (brandIds == null || brandIds.isEmpty()) {
            return null;
        }
        return (root, _, _) -> root.get("brand").get("id").in(brandIds);
    }

    /**
     * Фильтр по категориям
     */
    public static Specification<Product> categoryIn(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return (root, cq, _) -> {
            var categories = root.join("categories", JoinType.LEFT);
            if (cq != null) {
                cq.distinct(true);
            }
            return categories.get("id").in(categoryIds);
        };
    }

    /**
     * Комбинирует все фильтры
     */
    public static Specification<Product> withFilters(String query, List<Long> brandIds, List<Long> categoryIds) {
        return Specification
                .where(nameContains(query))
                .and(brandIn(brandIds))
                .and(categoryIn(categoryIds));
    }
}
