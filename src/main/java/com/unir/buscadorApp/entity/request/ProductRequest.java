package com.unir.buscadorApp.entity.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private  String  name;
    private  String  company;
    private Integer price;
    private  String description;
    private  String urlImage;
    private  String largeDescription;
    private  Integer score;
}
