package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.Tva;
import com.high4resto.comptabilite.documents.Vendor;
import com.high4resto.comptabilite.documents.VendorInvoice;
import com.high4resto.comptabilite.repository.TvaRepository;
import com.high4resto.comptabilite.utils.InvoiceUtil;
import com.high4resto.comptabilite.utils.MathUtil;
import com.high4resto.comptabilite.utils.SirenApiUtil;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component("invoice")
@SessionScope
public class VendorInvoiceView implements Serializable{
    @Getter @Setter
     VendorInvoice newInvoice=new VendorInvoice();

    @Getter @Setter
    private Double totalTTC=0.0;
    @Getter @Setter
    private Double totalHT=0.0;
    @Getter @Setter
    private Double totalTVA=0.0;

    @Autowired
    private FileUploadView document;
    @Autowired
    private UserLoginView userLoginView;
    @Autowired
    private TvaRepository tvaRepository;
    @Getter @Setter
    private InvoiceLine selectedLine;
    

    List<Tva> tvas=new ArrayList<Tva>();

    @PostConstruct
    public void init() {
        this.tvas=tvaRepository.findAll();
    }

    private void calculateTotal()
    {
        this.totalHT=0.0;
        this.totalTTC=0.0;
        this.totalTVA=0.0;
        double aTva=tvas.get(0).getValue();
        double bTva=tvas.get(1).getValue();
        double cTva=tvas.get(2).getValue();
        double dTva=tvas.get(3).getValue();

        int size=this.newInvoice.getInvoiceLines().size();
        for (int i = 0; i < size; i++) {
            this.totalHT+=this.newInvoice.getInvoiceLines().get(i).getHTtotalPrice();
            if(this.newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("A"))
            {
                this.totalTTC+=this.newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+aTva)/100;
            }
            else if(this.newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("B"))
            {
                this.totalTTC+=this.newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+bTva)/100;
            }
            else if(this.newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("C"))
            {
                this.totalTTC+=this.newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+cTva)/100;
            }
            else if(this.newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("D"))
            {
                this.totalTTC+=this.newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+dTva)/100;
            }
        }
        this.totalHT=MathUtil.round(this.totalHT, 2);
        this.totalTTC=MathUtil.round(this.totalTTC, 2);
        this.totalTVA=MathUtil.round(this.totalTTC-this.totalHT,2);
    }

    public void recalculateTotal()
    {
        this.calculateTotal();
    }
    
    public void importInvoice()
    {
        if(this.document.getSelectDocument()!=null)
        {
            this.newInvoice=InvoiceUtil.importInvoice(this.document.getSelectDocument().getBrut());
            this.calculateTotal();    
        }
    }

    public void newVendorInvoice()
    {
        this.newInvoice=new VendorInvoice();
    }
    public void setMyRib()
    {
        this.newInvoice.setOriginPayement(userLoginView.getUser().getRib());
    }
    public void addInvoiceLine()
    {
        this.newInvoice.getInvoiceLines().add(new InvoiceLine());
    }
    public void deleteInvoiceLine()
    {
        this.newInvoice.getInvoiceLines().remove(this.selectedLine);
        this.calculateTotal();
    }
    public void searchFromSiret()
    {
        Vendor tpVendor=SirenApiUtil.getVendorFromSiret(this.newInvoice.getIssuer().getSociety().getSiret());
        this.newInvoice.getIssuer().getSociety().setSiren(tpVendor.getSociety().getSiren());
        this.newInvoice.getIssuer().getSociety().setName(tpVendor.getSociety().getName());;
        this.newInvoice.getIssuer().getSociety().setAddress(tpVendor.getSociety().getAddress());
        this.newInvoice.getIssuer().getSociety().setApe(tpVendor.getSociety().getApe());
        this.newInvoice.getIssuer().getSociety().setTva_Number(tpVendor.getSociety().getTva_Number());
    }
}
