package com.unir.buscadorApp.entity.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(indexName = "products", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product {
    @Id
    private  String id;

    @Field(type = FieldType.Text, name = "name")
    private  String  name;

    @Field(type = FieldType.Keyword, name = "company")
    private  String  company;

    @Field(type = FieldType.Keyword, name = "price")
    private Integer price;

    @Field(type = FieldType.Search_As_You_Type, name = "description")
    private  String description;

    @Field(type = FieldType.Keyword, name = "urlImage")
    private  String urlImage;

    @Field(type = FieldType.Search_As_You_Type, name = "largeDescription")
    private  String largeDescription;

    @Field(type = FieldType.Keyword, name = "score")
    private  Integer score;

}