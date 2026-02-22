package mdek.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mdek.dto.FacetCount;
import mdek.dto.SearchResult;
import mdek.entity.Product;
import mdek.repository.BrandRepository;
import mdek.repository.CategoryRepository;
import mdek.repository.ProductRepository;
import mdek.repository.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    private static final int PAGE_SIZE = 20;

    @Transactional(readOnly = true)
    public SearchResult search(String query, List<Long> brandIds, List<Long> categoryIds, int page) {
        // Нормализация
        String q = normalize(query);
        List<Long> brands = normalizeList(brandIds);
        List<Long> categories = normalizeList(categoryIds);

        boolean hasBrandFilter = brands != null;
        boolean hasCategoryFilter = categories != null;

        // Для нативных запросов нужен непустой список
        List<Long> brandsForQuery = hasBrandFilter ? brands : List.of(-1L);
        List<Long> categoriesForQuery = hasCategoryFilter ? categories : List.of(-1L);

        // Поиск продуктов через Specification (чистый Criteria API)
        var spec = ProductSpecification.withFilters(q, brands, categories);
        var pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id"));
        Page<Product> products = productRepository.findAll(spec, pageable);

        // Фасеты (топ-20 по количеству продуктов)
        List<FacetCount> brandFacets = toBrandFacets(
                brandRepository.countProducts(q, categoriesForQuery, hasCategoryFilter));

        List<FacetCount> categoryFacets = toCategoryFacets(
                categoryRepository.countProducts(q, brandsForQuery, hasBrandFilter));

        return new SearchResult(products, brandFacets, categoryFacets);
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private List<Long> normalizeList(List<Long> list) {
        return (list == null || list.isEmpty()) ? null : list;
    }

    private List<FacetCount> toBrandFacets(List<Object[]> rows) {
        return rows.stream()
                .filter(row -> row[0] != null)
                .map(row -> new FacetCount(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .toList();
    }

    private List<FacetCount> toCategoryFacets(List<Object[]> rows) {
        return rows.stream()
                .filter(row -> row[0] != null)
                .map(row -> new FacetCount(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .toList();
    }
}
