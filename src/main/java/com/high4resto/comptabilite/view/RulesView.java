package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.documents.ComptabilityRules;
import com.high4resto.comptabilite.documents.ItemFilter;
import com.high4resto.comptabilite.documents.TransactionFilter;
import com.high4resto.comptabilite.services.implementations.ComptabilityRulesService;
import com.high4resto.comptabilite.services.implementations.ItemFilterService;
import com.high4resto.comptabilite.services.implementations.TransactionFilterService;
import com.high4resto.comptabilite.services.implementations.TransactionService;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component("rulesView")
@SessionScope
public class RulesView implements Serializable{

    @Autowired
    private ComptabilityRulesService comptabilityRulesService;
    @Autowired
    private TransactionFilterService transactionFilterService;
    @Autowired
    private ItemFilterService itemFilterService;
    @Autowired
    private TransactionService transactionService;

    @Getter @Setter
    private ComptabilityRules currentRules=new ComptabilityRules();
    @Getter @Setter
    private List<ComptabilityRules> comptabilityRulesList=new ArrayList<>();
    @Getter @Setter
    private List<TransactionFilter> transactionFilterList=new ArrayList<>();
    @Getter @Setter
    private List<ItemFilter> itemFilterList=new ArrayList<>();
    @Getter @Setter
    private TransactionFilter currentTransactionFilter=new TransactionFilter();
    @Getter @Setter
    private ItemFilter currentItemFilter=new ItemFilter();
    @Getter @Setter
    private TransactionFilter selectedTransactionFilter=new TransactionFilter();
    @Getter @Setter
    private ItemFilter selectedItemFilter=new ItemFilter();
    @Getter @Setter
    private double result;


    @PostConstruct
    public void init()
    {
        transactionFilterList=transactionFilterService.getAllTransactionFilters();
        itemFilterList=itemFilterService.getAllItemFilters();
        comptabilityRulesList=comptabilityRulesService.getAllComptabilityRules();
    }

    public void deleteTransactionFilter(TransactionFilter transactionFilter)
    {
        this.currentRules.getTransactionFilters().remove(transactionFilter);
        this.transactionFilterList.add(transactionFilter);
    }

    public void addTransactionFilter(TransactionFilter transactionFilter)
    {
        this.currentRules.getTransactionFilters().add(transactionFilter);
        this.transactionFilterList.remove(transactionFilter);
    }

    public void addItemFilter(ItemFilter itemFilter)
    {
        this.currentRules.getItemFilters().add(itemFilter);
        this.itemFilterList.remove(itemFilter);
    }

    public void deleteItemFilter(ItemFilter itemFilter)
    {
        this.currentRules.getItemFilters().remove(itemFilter);
        this.itemFilterList.add(itemFilter);
    }

    public void calculateResult()
    {
        this.result=this.currentRules.evaluate(this.transactionService.getAllTransactions());
    }
}
