package com.high4resto.comptabilite.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
public class ComptabilityRules {
    @Id
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String code;
    // List of transaction and items filters
    @Getter
    @Setter
    private List<TransactionFilter> transactionFilters;
    @Getter
    @Setter
    private List<ItemFilter> itemFilters;
    // Item field for operation
    @Getter
    @Setter
    private String fieldFor;

    // Sum, Mean, Median, Min, Max
    @Getter
    @Setter
    private String operator;

    public ComptabilityRules() {
        super();
        this.transactionFilters = new ArrayList<>();
        this.itemFilters = new ArrayList<>();
    }

    private List<Transaction> filtre(List<Transaction> transactions) {
        for (TransactionFilter transactionFilter : transactionFilters) {
            transactions = transactions.stream().filter(transactionFilter).collect(Collectors.toList());
        }
        for (Transaction transaction : transactions) {
            for (ItemFilter itemFilter : itemFilters) {
                transaction.setInvoiceLines(
                        transaction.getInvoiceLines().stream().filter(itemFilter).collect(Collectors.toList()));
            }
        }

        return transactions;
    }

    private double evaluateField(InvoiceLine line) {
        switch (fieldFor) {
            case "HTtotalPrice":
                return (double)line.getHTtotalPrice();
            case "TTCtotalPrice":
                return (double)line.getTTCtotalPrice();
            case "quantity":
                return (double)line.getQuantity();
            case "HTunitPrice":
                return (double)line.getHTunitPrice();
            case "TTCunitPrice":
                return (double)line.getTTCunitPrice();
            case "TVAPrice":
                return (double)line.getTVAPrice();
            default:
                return 0;
        }
    }

    public double evaluate(List<Transaction> transactions) {
        double result = 0;
        transactions = filtre(transactions);
        switch (this.operator) {
            case "SUM": {
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        result += evaluateField(line);
                    }
                }
                break;
            }
            case "MEAN": {
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        result += evaluateField(line);
                    }
                }
                result = result / transactions.size();
                break;
            }
            case "COUNT": {
                for (Transaction transaction : transactions) {
                    result += transaction.getInvoiceLines().size();
                }
                break;
            }
            case "MAX": {
                double max = 0;
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        if (evaluateField(line) > max) {
                            max = evaluateField(line);
                        }
                    }
                }
                result = max;
                break;
            }
            case "MIN": {
                double min = 10000;
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        if (evaluateField(line) < min) {
                            min = evaluateField(line);
                        }
                    }
                }
                result = min;
                break;
            }
            case "STANDARDDEVIATION": {
                double mean = 0;
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        mean += evaluateField(line);
                    }
                }
                mean = mean / transactions.size();
                double sum = 0;
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        sum += Math.pow(evaluateField(line) - mean, 2);
                    }
                }
                result = Math.sqrt(sum / transactions.size());
                break;
            }
            case "MEDIAN": {
                List<Double> values = new ArrayList<>();
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        values.add(evaluateField(line));
                    }
                }
                values.sort(null);
                if (values.size() % 2 == 0) {
                    result = (values.get(values.size() / 2) + values.get(values.size() / 2 - 1)) / 2;
                } else {
                    result = values.get(values.size() / 2);
                }
                break;
            }
            case "RANGE": {
                double min = 10000;
                double max = 0;
                for (Transaction transaction : transactions) {
                    for (InvoiceLine line : transaction.getInvoiceLines()) {
                        if (evaluateField(line) < min) {
                            min = evaluateField(line);
                        }
                        if (evaluateField(line) > max) {
                            max = evaluateField(line);
                        }
                    }
                }
                result = max - min;
                break;
            }
            default:
                break;
        }
        return result;
    }
}
