package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.high4resto.comptabilite.utils.TextUtil;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "invoice_line")
public class InvoiceLine {
    @Id
    private String Id;
    @Getter @Setter
    private Item item;
    @Getter @Setter
    private int quantity;
    @Getter @Setter
    private double cotSocial;
    @Getter @Setter
    private double HTunitPrice;
    @Getter @Setter
    private double HTtotalPrice;
    @Getter @Setter
    private double TTCunitPrice;
    @Getter @Setter
    private double TTCtotalPrice;
    @Transient
    @Getter @Setter
    private String rowKey;
    


    public InvoiceLine()
    {
        this.item=new Item();
        this.rowKey=TextUtil.getRandomString(20);
    }

    @Override
    public String toString()
    {
       return this.item.getCategory().getName()+"\t"+ this.item.getName()+" QTY:"+this.getQuantity()+" Prix unitaire HT:"+this.HTunitPrice+" Prix total HT:"+this.HTtotalPrice+" TVA:"+this.item.getTva().getCode(); 
    }
    
}
