package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tva")
public class Tva {
    @Id
    private String id;
    @Getter @Setter
    private String code;
    @Getter @Setter
    private double value;
    @Getter @Setter
    private String validity;
}
