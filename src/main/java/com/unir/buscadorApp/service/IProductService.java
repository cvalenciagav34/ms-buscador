package com.unir.buscadorApp.service;

import com.unir.buscadorApp.entity.pojo.Product;
import com.unir.buscadorApp.entity.request.ProductRequest;
import com.unir.buscadorApp.entity.response.ProductsResponse;

public interface IProductService {
    ProductsResponse getProducts(String name, String company,Integer price, String description
            ,String urlImage, String largeDescription, Integer score, Boolean aggregate);

    Product getProduct(String id);

    Boolean removeProduct(String id);

    Product createProduct(ProductRequest request);

    Product updateProduct(String id, ProductRequest productRequest);
}
