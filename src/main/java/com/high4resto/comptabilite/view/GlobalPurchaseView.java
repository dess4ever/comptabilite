package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.model.charts.pie.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.dataStruct.Camenbert;
import com.high4resto.comptabilite.dataStruct.NameValue;
import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.VendorInvoice;
import com.high4resto.comptabilite.repository.VendorInvoiceRepository;
import com.high4resto.comptabilite.utils.InvoiceUtil;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component
@SessionScope
public class GlobalPurchaseView implements Serializable {
    private static final long serialVersionUID = 1L;
    @Getter @Setter    
    private List<Date> range;
    @Getter @Setter   
    private PieChartModel byVendorModel;
    @Getter @Setter   
    private PieChartModel byCategoryModel;
    @Getter @Setter
    private boolean showChart=false;
    @Autowired
    private VendorInvoiceRepository vendorInvoiceRepository;
    private List<VendorInvoice> vendorInvoices;
    @PostConstruct
    public void init() {
    }
    public void createReport() {

        if(range==null || range.size()!=2) {
            return;
        }
        else
        {
            this.vendorInvoices = vendorInvoiceRepository.findAll();
            List<NameValue> nameValuesByVendor=new ArrayList<>();
            List<NameValue> nameValuesByCategory=new ArrayList<>();
            for(VendorInvoice invoice:vendorInvoices)
            {
                InvoiceUtil.calculateTotal(invoice, 2.2, 5.5, 10.0, 20.0);
                if(invoice.getInvoiceDate().after(range.get(0)) && invoice.getInvoiceDate().before(range.get(1)))
                {
                    nameValuesByVendor.add(new NameValue(invoice.getIssuer().getSociety().getName(), invoice.getTotalHT()));
                    for(InvoiceLine line:invoice.getInvoiceLines())
                    {
                        nameValuesByCategory.add(new NameValue(line.getItem().getCategory().getName(), line.getHTtotalPrice()));
                    }
                }
            }
            this.byVendorModel = new Camenbert(nameValuesByVendor).getChartModel();
            this.byCategoryModel = new Camenbert(nameValuesByCategory).getChartModel();
            this.showChart=true;    
        }
    }
}
