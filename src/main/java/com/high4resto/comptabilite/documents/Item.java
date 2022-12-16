package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "item")
public class Item {
    @Id
    private String id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String fact_Name;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private Double mean_Price;
    @Getter @Setter
    private Tva tva;
    @Getter @Setter
    private String packaging;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private Item_Category category;
    @Getter @Setter
    private Item_Category sub_Category;
    @Getter @Setter
    private String otherInfo;

}
