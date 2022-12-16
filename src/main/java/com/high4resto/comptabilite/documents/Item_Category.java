package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "item_Category")
public class Item_Category {
    @Id
    private String id;
    @Indexed @Getter @Setter
    private String name;
    @Getter @Setter
    private String comptability_Key;
  
}
