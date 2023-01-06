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
import com.high4resto.comptabilite.documents.Transaction;
import com.high4resto.comptabilite.services.implementations.TransactionService;

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
    private TransactionService transactionService;

    private List<Transaction> transactions;
    @PostConstruct
    public void init() {
    }
    public void createReport() {

        if(range==null || range.size()!=2) {
            return;
        }
        else
        {
            this.transactions = transactionService.getAllTransactions();
            List<NameValue> nameValuesByVendor=new ArrayList<>();
            List<NameValue> nameValuesByCategory=new ArrayList<>();
            for(Transaction transaction:transactions)
            {
                transactionService.calculateTotal(transaction);
                if(transaction.getInvoiceDate().after(range.get(0)) && transaction.getInvoiceDate().before(range.get(1)))
                {
                    nameValuesByVendor.add(new NameValue(transaction.getVendor().getSociety().getName(), transaction.getTotalHT()));
                    for(InvoiceLine line:transaction.getInvoiceLines())
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
