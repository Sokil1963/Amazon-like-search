package mdek.repository;

import mdek.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
    SELECT c.id, c.name, COUNT(*) as cnt
    FROM categories c
    JOIN product_categories pc ON pc.category_id = c.id
    JOIN products p ON p.id = pc.product_id
    WHERE (:query IS NULL OR p.name ILIKE '%' || CAST(:query AS TEXT) || '%')
      AND (:hasBrandFilter = false OR p.brand_id IN (:brandIds))
    GROUP BY c.id, c.name
    ORDER BY cnt DESC
    LIMIT 50
    """, nativeQuery = true)
    List<Object[]> countProducts(
            @Param("query") String query,
            @Param("brandIds") List<Long> brandIds,
            @Param("hasBrandFilter") boolean hasBrandFilter
    );



}
