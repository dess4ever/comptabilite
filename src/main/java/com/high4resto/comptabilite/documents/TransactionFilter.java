package com.high4resto.comptabilite.documents;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.high4resto.comptabilite.dataStruct.MiniFilter;
import com.high4resto.comptabilite.utils.TextUtil;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "transactionFilter")
public class TransactionFilter implements Predicate<Transaction> {

    @Id
    private String id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private List<List<MiniFilter>> filters;


    public TransactionFilter() {
        super();
        this.filters = new ArrayList<>();
    }

    private boolean evaluateOrCondition(List<MiniFilter> filters, Transaction transaction) {
        for (MiniFilter filter : filters) {
            String key=getValueOfFieldWithString(transaction, filter.getKey());
            if(TextUtil.testIfStringIsDateddMMyyyy(key))
            {
                Date dateKey;
                Date dateValue;
                try{
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    dateKey = dateFormat.parse(key);
                    dateValue = dateFormat.parse(filter.getValue());
                    switch(filter.getOperator())
                    {
                        case "equals":
                            if (dateKey.equals(dateValue))
                                return true;
                            break;
                        case "not":
                            if (!dateKey.equals(dateValue))
                                return true;
                            break;
                        case "greaterThan":
                            if (dateKey.compareTo(dateValue)>0)
                                return true;
                            break;
                        case "lessThan":
                            if (dateKey.compareTo(dateValue)<0)
                                return true;
                            break;
                        default:
                            break;
                    }
                }catch(Exception e){
                    return false;
                }
            }
            else
            switch(filter.getOperator())
            {
                case "equals":
                    if (key.equals(filter.getValue()))
                        return true;
                    break;
                case "contains":
                    if (key.contains(filter.getValue()))
                        return true;
                    break;
                case "not":
                    if (!key.equals(filter.getValue()))
                        return true;
                    break;
                case "greaterThan":
                    if (key.compareTo(filter.getValue())>0)
                        return true;
                    break;
                case "lessThan":
                    if (key.compareTo(filter.getValue())<0)
                        return true;
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private String getValueOfFieldWithString(Transaction transaction,String field){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        switch (field){
            case "refActivite":
                return transaction.getRefActivite();
            case "type":
                return transaction.getType();
            case "typeDocument":
                return transaction.getTypeDocument();
            case "number":
                return transaction.getNumber();
            case "invoiceDate":
                return dateFormat.format(transaction.getInvoiceDate());
            case "dueDate":
                return dateFormat.format(transaction.getDueDate());
            case "paymentDate":
                return dateFormat.format(transaction.getPayementDate());
            case "payementStatus":
                return transaction.getPayementStatus();
            case "paymentMethod":
                return transaction.getPayementMethod();
            case "destinationPayement.name":
                return transaction.getDestinationPayement().getName();
            case "destinationPayement.account_Number":
                return transaction.getDestinationPayement().getAccount_Number();
            case "destinationPayement.iban":
                return transaction.getDestinationPayement().getIban();
            case "originPayement.name":
                return transaction.getOriginPayement().getName();
            case "originPayement.account_Number":
                return transaction.getOriginPayement().getAccount_Number();
            case "originPayement.iban":
                return transaction.getOriginPayement().getIban();
            case "vendor.society.name":
                return transaction.getVendor().getSociety().getName();
            case "vendor.society.siret":
                return transaction.getVendor().getSociety().getSiret();
            case "vendor.society.siren":
                return transaction.getVendor().getSociety().getSiren();
            case "customer.name":
                return transaction.getCustomer().getName();
            case "customer.siret":
                return transaction.getCustomer().getSiret();
            case "customer.siren":
                return transaction.getCustomer().getSiren();
            default:
                return "";

        }
        
    }

    @Override @Transient
    public boolean test(Transaction arg0) {
        for (List<MiniFilter> list : filters) {
            if (!evaluateOrCondition(list, arg0))
                return false;
        }
        return true;
    }
    
}
