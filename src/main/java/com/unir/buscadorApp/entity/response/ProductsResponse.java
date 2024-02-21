package com.unir.buscadorApp.entity.response;

import com.unir.buscadorApp.entity.pojo.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductsResponse {
    private List<Product> products;
    private List<AggregationDetails> aggs;
}
