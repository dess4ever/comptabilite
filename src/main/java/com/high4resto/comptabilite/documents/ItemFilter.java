package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.high4resto.comptabilite.dataStruct.MiniFilter;
import com.high4resto.comptabilite.utils.TextUtil;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Document(collection = "itemFilter")
public class ItemFilter implements Predicate<InvoiceLine> {

    @Id
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private List<List<MiniFilter>> filters;

    public ItemFilter() {
        super();
        this.filters = new ArrayList<>();
    }

    private boolean evaluateOrCondition(List<MiniFilter> filters, InvoiceLine invoiceLine) {
        for (MiniFilter filter : filters) {
            String key = getValueOfFieldWithString(invoiceLine, filter.getKey());
            if(key==null)
                return false;
            if (TextUtil.testIfStringIsNumber(key)) {
                double numberKey = Double.parseDouble(key);
                double numberValue = Double.parseDouble(filter.getValue());
                switch (filter.getOperator()) {
                    case "equals":
                        if (numberKey == numberValue)
                            return true;
                        break;
                    case "not":
                        if (numberKey != numberValue)
                            return true;
                        break;
                    case "greaterThan":
                        if (numberKey > numberValue)
                            return true;
                        break;
                    case "lessThan":
                        if (numberKey < numberValue)
                            return true;
                        break;
                    default:
                        break;
                }
            }
            // Else this is text
            else {
                switch (filter.getOperator()) {
                    case "equals":
                        if (key.equals(filter.getValue()))
                            return true;
                        break;
                    case "not":
                        if (!key.equals(filter.getValue()))
                            return true;
                        break;
                    case "contains":
                        if (key.contains(filter.getValue()))
                            return true;
                        break;
                    default:
                        break;
                }
            }
        }

        return false;
    }

    private String getValueOfFieldWithString(InvoiceLine invoiceLine, String key) {
        switch (key) {
            case "quantity":
                return Integer.toString(invoiceLine.getQuantity());
            case "unitPrice":
                return Double.toString(invoiceLine.getHTunitPrice());
            case "totalPrice":
                return Double.toString(invoiceLine.getHTtotalPrice());
            case "item.factName":
                return invoiceLine.getItem().getFact_Name();
            case "item.category.name":
                return invoiceLine.getItem().getCategory().getName();
            case "item.tva.value":
                return Double.toString(invoiceLine.getItem().getTva().getValue());
            case "item.tva.code":
                return invoiceLine.getItem().getTva().getCode();
            default:
                return "";
        }
    }

    @Override
    @Transient
    public boolean test(InvoiceLine arg0) {
        for (List<MiniFilter> list : filters) {
            if (!evaluateOrCondition(list, arg0))
                return false;
        }
        return true;
    }

}
