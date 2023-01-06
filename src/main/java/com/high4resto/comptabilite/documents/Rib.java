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

    public int getStrenght()
    {
        int strenght=0;
        if(this.bank_Name!=null && !this.bank_Name.isEmpty())
            strenght++;
        if(this.bank_Code!=null && !this.bank_Code.isEmpty())
            strenght++;
        if(this.branch_Code!=null && !this.branch_Code.isEmpty())
            strenght++;
        if(this.account_Number!=null && !this.account_Number.isEmpty())
            strenght++;
        if(this.key!=null && !this.key.isEmpty())
            strenght++;
        if(this.iban!=null && !this.iban.isEmpty())
            strenght++;
        if(this.bic!=null && !this.bic.isEmpty())
            strenght++;
        if(this.ics!=null && !this.ics.isEmpty())
            strenght++;
        if(this.bank_Address!=null)
            strenght++;
        if(this.other_Info!=null && !this.other_Info.isEmpty())
            strenght++;
        if(this.order!=null && !this.order.isEmpty())
            strenght++;
        return strenght;
    }
}
