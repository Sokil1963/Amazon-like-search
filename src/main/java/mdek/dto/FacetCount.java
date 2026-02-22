package mdek.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FacetCount {
    private Long id;
    private String name;
    private Long count;
}
