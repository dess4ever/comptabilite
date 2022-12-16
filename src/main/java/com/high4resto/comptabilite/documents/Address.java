package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "address")
public class Address {
    @Id
    private String id;
    @Getter @Setter
    private String name;
    @Override
    public String toString()
    {
        String buffer=this.name+"\n";
        return buffer;
    }
}
