package com.high4resto.comptabilite.documents;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "transaction")
public class Transaction {
    @Id
    @Getter
    private String id;
    @Getter @Setter
    private String number;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String typeDocument;
    @Getter @Setter
    private Vendor vendor;
    @Getter @Setter
    private Society customer;
    @Getter @Setter
    private List<InvoiceLine> invoiceLines;
    @Getter @Setter
    private Date payementDate;
    @Getter @Setter
    private Date dueDate;
    @Getter @Setter
    private String payementStatus;
    @Getter @Setter
    private Date invoiceDate;
    @Getter @Setter
    private String payementMethod;
    @Getter @Setter
    private Rib originPayement; // Compte débiteur
    @Getter @Setter
    private Rib destinationPayement; // Compte créditeur
    @Getter @Setter
    private String refDocument;
    @Getter @Setter
    private String refActivite;
    @Getter @Setter @Transient
    private double totalTTC;
    @Getter @Setter @Transient
    private double totalHT;
    @Getter @Setter @Transient
    private double totalTVA;
    @Transient
    @Getter @Setter
    private double totalTVAA;
    @Transient
    @Getter @Setter
    private double totalTVAB;
    @Transient
    @Getter @Setter
    private double totalTVAC;
    @Transient
    @Getter @Setter
    private double totalTVAD;
    @Transient
    private LocalDate invoiceDateC;

    public LocalDate getInvoiceDateC() {
        //convert date to localdate
        if(invoiceDate!=null)
        {
            invoiceDateC=invoiceDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        else
        {
            invoiceDateC=LocalDate.now();
        }
        return invoiceDateC;
    }

    public Transaction()
    {
        this.invoiceLines=new ArrayList<InvoiceLine>();
        this.vendor=new Vendor();
        this.customer=new Society();
        this.originPayement=new Rib();
        this.destinationPayement=new Rib();
    }
}
