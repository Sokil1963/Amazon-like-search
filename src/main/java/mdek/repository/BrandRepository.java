package mdek.repository;

import mdek.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Подсчёт продуктов по брендам с учётом фильтров
     */
    @Query(value = """
            SELECT b.id, b.name, COUNT(DISTINCT p.id) as cnt
            FROM brands b
            JOIN products p ON b.id = p.brand_id
            LEFT JOIN product_categories pc ON p.id = pc.product_id
            WHERE (:query IS NULL OR p.name ILIKE '%' || CAST(:query AS TEXT) || '%')
              AND (:hasCategoryFilter = false OR pc.category_id IN (:categoryIds))
            GROUP BY b.id, b.name
            ORDER BY cnt DESC
            LIMIT 20
            """, nativeQuery = true)
    List<Object[]> countProducts(
            @Param("query") String query,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("hasCategoryFilter") boolean hasCategoryFilter
    );
}
