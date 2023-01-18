package com.high4resto.comptabilite.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.dataStruct.MiniFilter;
import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.ItemFilter;
import com.high4resto.comptabilite.documents.Transaction;
import com.high4resto.comptabilite.services.implementations.ItemFilterService;
import com.high4resto.comptabilite.services.implementations.TransactionService;
import com.high4resto.comptabilite.utils.PrimefaceUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import lombok.Getter;
import lombok.Setter;

@Component("itemFilterView")
@SessionScope
public class ItemFilterView {
    @Getter @Setter
    private List<SelectItem> componentsSelectionItem;
    @Getter @Setter
    private MiniFilter currentFilter;
    @Getter @Setter
    private List<ItemFilter> itemFilters;
    @Getter @Setter
    private List<InvoiceLine> invoiceLines;
    @Getter @Setter
    private ItemFilter itemFilter;

    private String currentKeyForDictionnary="";

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ItemFilterService itemFilterService;

    @PostConstruct
    public void init() {
        this.itemFilters = itemFilterService.getAllItemFilters();
        this.currentFilter = new MiniFilter();
        itemFilter = new ItemFilter();
        this.componentsSelectionItem = new ArrayList<>();
        SelectItemGroup generalGroup= new SelectItemGroup("Valeur de l'item");
        generalGroup.setSelectItems(new SelectItem[]{
            new SelectItem("quantity","Quantité"),
            new SelectItem("unitPrice","Prix unitaire hors taxe"),
            new SelectItem("totalPrice","Prix total hors taxe"),
            new SelectItem("item.factName","Nom du produit"),
            new SelectItem("item.category.name","Catégorie du produit"),
            new SelectItem("item.tva.value","Taux de taxe"),
            new SelectItem("item.tva.code","Code de taxe"),
        });
       componentsSelectionItem.add(generalGroup); 
    }

    public void onItemSelectedListener(SelectEvent<String> event){
        this.currentKeyForDictionnary = event.getObject();

    }

    public List<String> complete(String query) {
        List<String> results = new ArrayList<>();
        if (currentKeyForDictionnary.equals("item.factName"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("ItemName").getAll();
            else
                return transactionService.getTreeDictionary().get("ItemName").getList(query, 0);
        }
        else if (currentKeyForDictionnary.equals("item.category.name"))
        {
            if(query.equals(" "))
                return transactionService.getTreeDictionary().get("ItemCategory").getAll();
            else
                return transactionService.getTreeDictionary().get("ItemCategory").getList(query, 0);
        }
        return results;
    }

    public void addFilter() {
        List<MiniFilter> tFilters=new ArrayList<>();
        tFilters.add(currentFilter);
        itemFilter.getFilters().add(tFilters);
        currentFilter = new MiniFilter();
    }

    public void addOrFilter(int primaryIndex)
    {
        this.itemFilter.getFilters().get(primaryIndex).add(currentFilter);
        currentFilter = new MiniFilter();
    }

    public void removeFilter(int primaryIndex, int secondaryIndex)
    {
        if (secondaryIndex==-1)
            this.itemFilter.getFilters().remove(primaryIndex);
        else
            this.itemFilter.getFilters().get(primaryIndex).remove(secondaryIndex);

            if(this.itemFilter.getFilters().get(primaryIndex).isEmpty())
                this.itemFilter.getFilters().remove(primaryIndex);
        }
    
    public void save()
    {
        PrimefaceUtil.addMessages(itemFilterService.saveItemFilter(itemFilter));
        this.itemFilters = itemFilterService.getAllItemFilters();
        this.itemFilter = new ItemFilter();
    }

    public void testFilter()
    {

        List<Transaction> transactions = transactionService.getAllTransactions();
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        for (Transaction transaction : transactions) {
            invoiceLines.addAll(transaction.getInvoiceLines());
        }
        this.invoiceLines = invoiceLines.stream().filter(itemFilter).collect(Collectors.toList());
    }

    public void editFilter(ItemFilter itemFilter)
    {
        this.itemFilter = itemFilter;
        PrimefaceUtil.info("Modification du filtre dans le formulaire");
    }

    public void deleteFilter(ItemFilter itemFilter)
    {
        PrimefaceUtil.addMessages(itemFilterService.deleteItemFilter(itemFilter));
        this.itemFilters = itemFilterService.getAllItemFilters();
    }

}
