package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
public class Rib {
    @Id 
    private String id;
    @Indexed
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String bank_Name;
    @Getter @Setter
    private String bank_Code;
    @Getter @Setter
    private String branch_Code;
    @Getter @Setter
    private String account_Number;
    @Getter @Setter
    private String key;
    @Getter @Setter
    private String iban;
    @Getter @Setter
    private String bic;
    @Getter @Setter
    private String ics;
    @Getter @Setter
    private Address bank_Address;
    @Getter @Setter
    private String other_Info;
    @Getter @Setter
    private String order;

    public Rib()
    {
        this.bank_Address=new Address();
    }
}
