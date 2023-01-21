package com.high4resto.comptabilite.documents;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
public class ComptabilityRules {
    @Id
    private String id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String code;
    // List of transaction and items filters
    @Getter @Setter
    private List<TransactionFilter> transactionFilters;
    @Getter @Setter
    private List<ItemFilter> itemFilters;
    // Item field for operation
    @Getter @Setter
    private String fieldFor;
    
    // Sum, Mean, Median, Min, Max
    @Getter @Setter
    private String operation;
}
