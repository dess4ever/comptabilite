package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
public class CompteComptale {
    @Id
    private String id;
    @Getter @Setter
    @Indexed(unique = true)
    private String code;
    @Getter @Setter
    private String libelle;
    @Getter @Setter
    private String contrepartie;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String example;
    @Getter @Setter
    private boolean isDebit;
    @Getter @Setter
    private boolean isTerminate;
}
