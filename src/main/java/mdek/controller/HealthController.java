package mdek.controller;

import lombok.RequiredArgsConstructor;
import mdek.repository.BrandRepository;
import mdek.repository.CategoryRepository;
import mdek.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/api/health")
    public Map<String, Long> health() {
        return Map.of(
                "products", productRepository.count(),
                "brands", brandRepository.count(),
                "categories", categoryRepository.count()
        );
    }
}
