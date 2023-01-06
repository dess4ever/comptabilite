package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;


import com.high4resto.comptabilite.dataStruct.TreeDictionnary;
import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.Transaction;
import com.high4resto.comptabilite.services.implementations.SirenApiService;
import com.high4resto.comptabilite.services.implementations.TransactionService;
import com.high4resto.comptabilite.utils.PrimefaceUtil;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component("transactionView")
@SessionScope
public class TransactionView implements Serializable{

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserLoginView userLoginView;
    @Autowired
    private SirenApiService sirenApiService;
    @Autowired 
    private FileUploadView documentView;

    @Getter @Setter
    private Date FilterDateBegin;
    @Getter @Setter
    private Date FilterDateEnd;

    @Getter @Setter
    private List<Transaction> transactions;
    @Getter @Setter
    private List<Transaction> filteredTransactions;
    @Getter @Setter
    private Transaction selectedTransaction;
    @Getter @Setter
    private Transaction currentTransaction;
    
    private Map<String, TreeDictionnary> treeDictionnary;

    @Getter @Setter
    private InvoiceLine selectedLine=new InvoiceLine();
    @Getter @Setter
    private String textForItem;

    @PostConstruct
    public void init() {
        transactions=transactionService.getAllTransactions();
        /* Generate TreeDictionnary with the following:
        - linkedActivity
        - typeDocument
        - statusOfPayement
        - accountName
        - accountNumber
        - societyName
        - ItemName
        - ItemCategory
         */
       this.treeDictionnary=transactionService.getTreeDictionary();
        currentTransaction=new Transaction();
    }

    public void onRowSelect(SelectEvent<Transaction> event) {
        currentTransaction=event.getObject();
        this.calculateTotal();
    }

    public void addSocietyRibToCreditedAccount()
    {
        currentTransaction.setOriginPayement(userLoginView.getUser().getRib());
        currentTransaction.getOriginPayement().setName(userLoginView.getUser().getSociety().getName());

    }

    public void addSocietyRibToDebitedAccount()
    {
        currentTransaction.setDestinationPayement(userLoginView.getUser().getRib());
        currentTransaction.getDestinationPayement().setName(userLoginView.getUser().getSociety().getName());
    }

    public void addSocietyToVendor()
    {
        currentTransaction.getVendor().setSociety(userLoginView.getUser().getSociety());
    }

    public void addSocietyToCustomer()
    {
        currentTransaction.setCustomer(userLoginView.getUser().getSociety());
    }

    public List<String> completeLinkedActyvity(String query) {
        return treeDictionnary.get("linkedActivity").getList(query, 10);
    }

    public List<String> completeTypeDocument(String query) {
        return treeDictionnary.get("typeDocument").getList(query, 10);
    }

    public List<String> completeType(String query) {
        return treeDictionnary.get("type").getList(query, 10);
    }

    public List<String> completeStatusOfPayement(String query) {
        return treeDictionnary.get("statusOfPayement").getList(query, 10);
    }

    public List<String> completeAccountName(String query) {
        return treeDictionnary.get("accountName").getList(query, 10);
    }

    public List<String> completeSocietyName(String query) {
        return treeDictionnary.get("societyName").getList(query, 10);
    }

    public List<String> completeCategoryItem(String query) {
        return treeDictionnary.get("ItemCategory").getList(query, 10);
    }
                
    public void onCreditedAccountSelect(SelectEvent<String> event)
    {
        currentTransaction.setDestinationPayement(transactionService.getRib(event.getObject()));
    }

    public void onDebitedAccountSelect(SelectEvent<String> event)
    {
        currentTransaction.setOriginPayement(transactionService.getRib(event.getObject()));
    }

    public void onVendorSelect(SelectEvent<String> event)
    {
        currentTransaction.getVendor().setSociety(transactionService.getSociety(event.getObject()));
    }

    public void onCustomerSelect(SelectEvent<String> event)
    {
        currentTransaction.setCustomer(transactionService.getSociety(event.getObject()));
    }

    public void searchFromSiret()
    {
        if(currentTransaction.getVendor().getSociety().getSiret()!=null && !currentTransaction.getVendor().getSociety().getSiret().isEmpty())
        {
            currentTransaction.setVendor(sirenApiService.getVendorFromSiret(currentTransaction.getVendor().getSociety().getSiret()));
        }
    }
    public void searchFromSiretCustomer()
    {
        if(currentTransaction.getCustomer().getSiret()!=null && !currentTransaction.getCustomer().getSiret().isEmpty())
        {
            currentTransaction.setCustomer(sirenApiService.getCustomerFromSiret(currentTransaction.getCustomer().getSiret()));
        }
    }

    public void fromDocument()
    {
        this.currentTransaction=transactionService.importTransaction(documentView.getSelectDocument().getDocument().getBrut());
        this.currentTransaction.setRefDocument(documentView.getSelectDocument().getDocument().getHash());
        this.calculateTotal();
        this.textForItem=documentView.getSelectDocument().getDocument().getBrut();
        PrimefaceUtil.info("Facture importée dans le formulaire transaction en cours");
    }
    public void calculateTotal()
    {
        currentTransaction=transactionService.calculateTotal(currentTransaction);
    }

    // Save the modification of the invoice
    public void onRowEdit(RowEditEvent<Transaction> event) {
        this.calculateTotal();
    }

    // Cancel the modification of the invoice
    public void onRowCancel(RowEditEvent<Transaction> event) {
        PrimefaceUtil.info("Modification annulée");
    }

    public void addTransactionLine()
    {
        currentTransaction.getInvoiceLines().add(new InvoiceLine());
    }

    public void openAiInvoiceLineAnalizer()
    {
        this.currentTransaction.setInvoiceLines(transactionService.getInvoiceLineWhitOpenAI(textForItem));
        this.calculateTotal();
    }

    public void deleteTransactionLine()
    {
        currentTransaction.getInvoiceLines().remove(selectedLine);
        this.calculateTotal();
    }

}
