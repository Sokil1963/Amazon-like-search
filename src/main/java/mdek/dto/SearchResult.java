package mdek.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mdek.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private Page<Product> products;
    private List<FacetCount> brandFacets;
    private List<FacetCount> categoryFacets;
}
