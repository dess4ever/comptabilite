package com.high4resto.comptabilite.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.dataStruct.MiniFilter;
import com.high4resto.comptabilite.documents.Transaction;
import com.high4resto.comptabilite.documents.TransactionFilter;
import com.high4resto.comptabilite.services.implementations.TransactionFilterService;
import com.high4resto.comptabilite.services.implementations.TransactionService;
import com.high4resto.comptabilite.utils.PrimefaceUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import lombok.Getter;
import lombok.Setter;

@Component("transactionFilterView")
@SessionScope
public class TransactionFilterView {
    @Getter @Setter
    private List<SelectItem> componentsSelectionTransaction;
    @Getter @Setter
    private MiniFilter currentFilter;
    @Getter @Setter
    private List<TransactionFilter> transactionFilters;
    @Getter @Setter
    private List<Transaction> transactions;
    @Getter @Setter
    private TransactionFilter transactionFilter;


    private String currentKeyForDictionnary="";

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionFilterService transactionFilterService;

    @PostConstruct
    public void init() {
        this.transactionFilters = transactionFilterService.getAllTransactionFilters();
        this.currentFilter = new MiniFilter();
        transactionFilter = new TransactionFilter();
        this.componentsSelectionTransaction = new ArrayList<>();
        SelectItemGroup generalGroup= new SelectItemGroup("Informations générales");
        generalGroup.setSelectItems(new SelectItem[]{
            new SelectItem("refActivite","Activité liée"),
            new SelectItem("type","Type de transaction"),
            new SelectItem("typeDocument","Type de document"),
            new SelectItem("number","Numéro de document"),
            new SelectItem("invoiceDate","Date du document"),
            new SelectItem("dueDate","Date d'échéance"),
            new SelectItem("payementDate","Date de paiement"),
            new SelectItem("payementStatus","Statut de paiement"),
            new SelectItem("payementMethod","Méthode de paiement")
        });
        SelectItemGroup creditedGroup= new SelectItemGroup("Compte crédité");
        creditedGroup.setSelectItems(new SelectItem[]{
            new SelectItem("destinationPayement.name","Nom (Compte crédité)"),
            new SelectItem("destinationPayement.account_Number","Numéro de compte (Compte crédité)"),
            new SelectItem("destinationPayement.iban","IBAN (Compte crédité)"),
        });
        SelectItemGroup debitedGroup= new SelectItemGroup("Compte débité");
        debitedGroup.setSelectItems(new SelectItem[]{
            new SelectItem("originPayement.name","Nom (Compte débité)"),
            new SelectItem("originPayement.account_Number","Numéro de compte (Compte débité)"),
            new SelectItem("originPayement.iban","IBAN (Compte débité)"),
        });
        SelectItemGroup vendorGroup= new SelectItemGroup("Fournisseur");
        vendorGroup.setSelectItems(new SelectItem[]{
            new SelectItem("vendor.society.name","Nom de la société (Fournisseur)"),
            new SelectItem("vendor.society.siret","Siret (Fournisseur)"),
            new SelectItem("vendor.society.siren","Siren (Fournisseur)"),
        });
        SelectItemGroup customerGroup= new SelectItemGroup("Client");
        customerGroup.setSelectItems(new SelectItem[]{
            new SelectItem("customer.name","Nom de la société (Client)"),
            new SelectItem("customer.siret","Siret (Client)"),
            new SelectItem("customer.siren","Siren (Client)"),
        });
        componentsSelectionTransaction.add(generalGroup);
        componentsSelectionTransaction.add(creditedGroup);
        componentsSelectionTransaction.add(debitedGroup);
        componentsSelectionTransaction.add(vendorGroup);
        componentsSelectionTransaction.add(customerGroup);
        
    }

    public void onItemSelectedListener(SelectEvent<String> event){
        this.currentKeyForDictionnary = event.getObject();

    }

    public List<String> complete(String query) {
        List<String> results = new ArrayList<>();
        if(currentKeyForDictionnary.equals("refActivite"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("linkedActivity").getAll();
            else
            return transactionService.getTreeDictionary().get("linkedActivity").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("type"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("type").getAll();
            else
            return transactionService.getTreeDictionary().get("type").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("typeDocument"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("typeDocument").getAll();
            else
            return transactionService.getTreeDictionary().get("typeDocument").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("payementStatus"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("statusOfPayement").getAll();
            else
            return transactionService.getTreeDictionary().get("statusOfPayement").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("payementMethod"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("payementMethod").getAll();
            else
            return transactionService.getTreeDictionary().get("payementMethod").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("destinationPayement.name"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("accountName").getAll();
            else
            return transactionService.getTreeDictionary().get("accountName").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("originPayement.name"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("accountName").getAll();
            else
            return transactionService.getTreeDictionary().get("accountName").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("vendor.society.name"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("societyName").getAll();
            else
            return transactionService.getTreeDictionary().get("societyName").getList(query, 10);
        }
        else if(currentKeyForDictionnary.equals("customer.name"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("societyName").getAll();
            else
            return transactionService.getTreeDictionary().get("societyName").getList(query, 10);
        }
        return results;
    }

    public void addFilter() {
        List<MiniFilter> tFilters=new ArrayList<>();
        tFilters.add(currentFilter);
        transactionFilter.getFilters().add(tFilters);
        currentFilter = new MiniFilter();
    }
    
    public void addOrFilter(int primaryIndex)
    {
        this.transactionFilter.getFilters().get(primaryIndex).add(currentFilter);
        currentFilter = new MiniFilter();
    }

    public void removeFilter(int primaryIndex, int secondaryIndex)
    {
        if(secondaryIndex==-1)
        {
            this.transactionFilter.getFilters().remove(primaryIndex);
        }
        else
        {
            this.transactionFilter.getFilters().get(primaryIndex).remove(secondaryIndex);
        }
        if(this.transactionFilter.getFilters().get(primaryIndex).isEmpty())
        {
            this.transactionFilter.getFilters().remove(primaryIndex);
        }
    }

    public void save()
    {
        PrimefaceUtil.addMessages(transactionFilterService.saveTransactionFilter(transactionFilter));
        this.transactionFilters=transactionFilterService.getAllTransactionFilters();
        this.transactionFilter = new TransactionFilter();
    }

    public void testFilter()
    {
        this.transactions=transactionService.getAllTransactions().stream().filter(transactionFilter).collect(Collectors.toList());
    }
    
    public void editFilter(TransactionFilter filter)
    {
        this.transactionFilter=filter;
        PrimefaceUtil.info("Modification du filtre dans le formulaire");
    }

    public void deleteFilter(TransactionFilter filter)
    {
        this.transactionFilterService.deleteTransactionFilter(filter);
        this.transactionFilters=transactionFilterService.getAllTransactionFilters();
        PrimefaceUtil.info("Suppression du filtre");
    }

}
