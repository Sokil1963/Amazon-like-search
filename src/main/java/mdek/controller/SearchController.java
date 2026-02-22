package mdek.controller;

import lombok.RequiredArgsConstructor;
import mdek.dto.SearchResult;
import mdek.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final ProductService productService;

    @GetMapping("/")
    public String search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<Long> brands,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        SearchResult result = productService.search(q, brands, categories, page);

        model.addAttribute("products", result.getProducts());
        model.addAttribute("brandFacets", result.getBrandFacets());
        model.addAttribute("categoryFacets", result.getCategoryFacets());
        model.addAttribute("query", q != null ? q : "");
        model.addAttribute("selectedBrands", brands != null ? brands : List.of());
        model.addAttribute("selectedCategories", categories != null ? categories : List.of());
        model.addAttribute("currentPage", page);

        return "search";
    }
}
