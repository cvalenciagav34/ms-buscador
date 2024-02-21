package com.unir.buscadorApp.service;


import com.unir.buscadorApp.data.DataAccessRepository;
import com.unir.buscadorApp.entity.pojo.Product;
import com.unir.buscadorApp.entity.request.ProductRequest;

import com.unir.buscadorApp.entity.response.ProductsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
public class ProductService implements IProductService {

    @Autowired
    private DataAccessRepository repository;
    @Override
    public ProductsResponse getProducts(String name, String company,Integer price, String description
            ,String urlImage, String largeDescription, Integer score, Boolean aggregate) {

        return repository.findProducts(name, company, price, description, urlImage, largeDescription,  score, aggregate);
    }

    @Override
    public Product getProduct(String id) {
        return repository.findById(id);
    }

    @Override
    public Boolean removeProduct(String id) {
        Product product = repository.findById(id);

        if (product != null) {
            repository.delete(product);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Product createProduct(ProductRequest productRequest) {
        if ( (productRequest != null)
                && StringUtils.hasLength(productRequest.getName().trim())
                && StringUtils.hasLength(productRequest.getCompany().trim())
                && StringUtils.hasLength(productRequest.getPrice().toString().trim())
                && StringUtils.hasLength(productRequest.getDescription().trim())
                && StringUtils.hasLength(productRequest.getUrlImage().trim())
                && StringUtils.hasLength(productRequest.getLargeDescription().trim())
                && StringUtils.hasLength(productRequest.getScore().toString().trim())
        ) {

            Product product = Product.builder()
                    .id(String.valueOf(productRequest.getName().hashCode()))
                    .name(productRequest.getName())
                    .company(productRequest.getCompany())
                    .price(productRequest.getPrice())
                    .description(productRequest.getDescription())
                    .urlImage(productRequest.getUrlImage())
                    .largeDescription(productRequest.getLargeDescription())
                    .score(productRequest.getScore()).build();

            return repository.saveOrUpdate(product);
        } else {
            return null;
        }
    }

    @Override
    public Product updateProduct(String id, ProductRequest productRequest) {
        Product product = repository.findById(id);
        product.setName(productRequest.getName());
        product.setCompany(productRequest.getCompany());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setLargeDescription(productRequest.getLargeDescription());
        product.setUrlImage(productRequest.getUrlImage());
        product.setScore(productRequest.getScore());

        return repository.saveOrUpdate(product);
    }
}