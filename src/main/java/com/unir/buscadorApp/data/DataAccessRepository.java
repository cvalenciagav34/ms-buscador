package com.unir.buscadorApp.data;

import java.util.*;

import com.unir.buscadorApp.entity.pojo.Product;
import com.unir.buscadorApp.entity.response.AggregationDetails;
import com.unir.buscadorApp.entity.response.ProductsResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {
    @Value("${server.fullAddress}")
    private String serverFullAddress;

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] descriptionSearchFields = {"description", "description._2gram", "description._3gram"};
    private final String[] largeDescriptionFields = {"largeDescription", "largeDescription._2gram", "largeDescription._3gram"};

    public Product saveOrUpdate(Product product) {
        return productRepository.save(product);
    }


    public Boolean delete(Product product) {
        productRepository.delete(product);
        return Boolean.TRUE;
    }

    public Product findById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    @SneakyThrows
    public ProductsResponse findProducts(String name, String company,Integer price, String description
            ,String urlImage, String largeDescription, Integer score, Boolean aggregate) {
        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(name)) {
            querySpec.must(QueryBuilders.matchQuery("name", name));
        }

        if (!StringUtils.isEmpty(company)) {
            querySpec.must(QueryBuilders.termQuery("company", company));
        }

        if (!StringUtils.isEmpty(price== -1 ? "":price.toString())) {
            querySpec.must(QueryBuilders.termQuery("price", price ));
        }

        if (!StringUtils.isEmpty(description)) {
            querySpec.must(QueryBuilders.multiMatchQuery(description, descriptionSearchFields).type(Type.BOOL_PREFIX));
        }

        if (!StringUtils.isEmpty(urlImage)) {
            querySpec.must(QueryBuilders.termQuery("urlImage", urlImage));
        }

        if (!StringUtils.isEmpty(largeDescription)) {
            querySpec.must(QueryBuilders.multiMatchQuery(largeDescription, largeDescriptionFields).type(Type.BOOL_PREFIX));
        }

        if (!StringUtils.isEmpty(score == -1?"": score.toString())) {
            querySpec.must(QueryBuilders.termQuery("score", score));
        }

        //Si no he recibido ningun parametro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Company Aggregation").field("company").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0);
        }

        //Opcionalmente, podemos paginar los resultados
        //nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 10));

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Product> result = elasticClient.search(query, Product.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms companyAgg = (ParsedStringTerms) aggs.get("Company Aggregation");

            //Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
            String queryParams = getQueryParams(name, company, price, description, urlImage, largeDescription, score);
            companyAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            serverFullAddress + "/products?company=" + bucket.getKey() + queryParams)));
        }
        return new ProductsResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    /**
     * Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
     *
     * @param name        - nombre del producto
     * @param company     - Empresa del producto
     * @param price       - precio del producto
     * @param description - descripciòn del producto
     * @param urlImage    - url de imagen del producto
     * @param largeDescription  - descripción larga del producto
     * @param score     - stock del producto
     * @return
     */
    private String getQueryParams(String name, String company,Integer price, String description
            ,String urlImage, String largeDescription, Integer score) {
        String queryParams = (StringUtils.isEmpty(name) ? "" : "&name=" + name)
                + (StringUtils.isEmpty(price == -1? "":price.toString()) ? "" : "&price=" + price)
                + (StringUtils.isEmpty(description) ? "" : "&description=" + description)
                + (StringUtils.isEmpty(urlImage) ? "" : "&urlImage=" + urlImage)
                + (StringUtils.isEmpty(largeDescription) ? "" : "&largeDescription=" + largeDescription)
                + (StringUtils.isEmpty(score == -1 ? "": score.toString()) ? "" : "&score=" + score);
        // Eliminamos el ultimo & si existe
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }
}
