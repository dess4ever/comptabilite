package com.high4resto.comptabilite.documents;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "vendorInvoice")
public class VendorInvoice {
    @Id
    private String id;
    @Indexed
    @Getter @Setter
    private String number;
    @Getter @Setter
    private Vendor issuer;
    @Getter @Setter
    private List<InvoiceLine> invoiceLines;
    @Getter @Setter
    private String date_Of_Payment;
    @Getter @Setter
    private String date_Of_Invoice;
    @Getter @Setter
    private String payementMethod;
    @Getter @Setter
    private Rib originPayement; 
    @Getter @Setter
    private String refDocument;
    @Getter @Setter @Transient
    private double totalTTC;
    @Getter @Setter @Transient
    private double totalHT;
    @Getter @Setter @Transient
    private double totalTVA;
    @Getter @Setter @Transient
    private double totalTVA55;
    @Getter @Setter @Transient
    private double totalTVA10;
    @Getter @Setter @Transient
    private double totalTVA20;

    public VendorInvoice()
    {
        this.invoiceLines=new ArrayList<InvoiceLine>();
        this.issuer=new Vendor();
        this.originPayement=new Rib();
    }

    @Override
    public String toString()
    {        
        String buffer="";
        buffer+="Facture n°"+this.number+"\n";
        buffer+="Reçue le:"+this.date_Of_Invoice+"\n";
        buffer+="Payée le :"+this.date_Of_Payment+"\n";
        buffer+=issuer.toString()+"\n";
        for(InvoiceLine line:invoiceLines)
        {
            buffer+=line.toString()+"\n";
        }
        return buffer;
    } 
}
