package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.high4resto.comptabilite.documents.UploadedDocument;
import com.high4resto.comptabilite.documents.VendorInvoice;
import com.high4resto.comptabilite.repository.UploadedDocumentRepository;
import com.high4resto.comptabilite.utils.InvoiceUtil;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component("invoice")
@ApplicationScope
public class VendorInvoiceView implements Serializable{
    @Getter @Setter
    VendorInvoice newInvoice=new VendorInvoice();
    @Getter @Setter
    List<UploadedDocument> invoicesFiles=new ArrayList<UploadedDocument>();
    @Getter @Setter
    UploadedDocument selectedFiles;
    @Autowired
    private transient UploadedDocumentRepository documentController;

    @PostConstruct
    public void init() {
        invoicesFiles=documentController.findAll();
    }
    
    public void importInvoice()
    {
        this.newInvoice=InvoiceUtil.importInvoice(this.selectedFiles.getContent());
    }

    public void newVendorInvoice()
    {
        this.newInvoice=new VendorInvoice();
    }
}
