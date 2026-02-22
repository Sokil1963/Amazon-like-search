package mdek.repository;

import mdek.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query(value = """
    SELECT b.id, b.name, COUNT(*) as cnt
    FROM brands b
    JOIN products p ON p.brand_id = b.id
    WHERE (:query IS NULL OR p.name ILIKE '%' || CAST(:query AS TEXT) || '%')
      AND (
          :hasCategoryFilter = false OR EXISTS (
              SELECT 1
              FROM product_categories pc
              WHERE pc.product_id = p.id
                AND pc.category_id IN (:categoryIds)
          )
      )
    GROUP BY b.id, b.name
    ORDER BY cnt DESC
    LIMIT 50
    """, nativeQuery = true)
    List<Object[]> countProducts(
            @Param("query") String query,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("hasCategoryFilter") boolean hasCategoryFilter
    );
}
