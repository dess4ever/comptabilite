package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "vendor")
public class Vendor {
    @Id
    private String id;
    @Getter @Setter
    private Society society;
    @Getter @Setter
    private Rib rib;
    @Getter @Setter
    private String approval;
    @Getter @Setter
    private String other_Info;
    @Getter @Setter
    private String comptabilityKey;

    public Vendor()
    {
        this.society=new Society();
        this.rib=new Rib();
    }

    @Override
    public String toString()
    {
        String buffer="";
        buffer+=society.toString()+"\n";
        buffer+="Numéro d'agrément sanitaire:"+approval+"\n";
        return buffer;     
    }
}
