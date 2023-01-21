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
import com.high4resto.comptabilite.documents.Tva;
import com.high4resto.comptabilite.repository.TvaRepository;
import com.high4resto.comptabilite.services.implementations.SirenApiService;
import com.high4resto.comptabilite.services.implementations.TransactionService;
import com.high4resto.comptabilite.utils.MathUtil;
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
    @Autowired
    private TvaRepository tvaRepository;

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
        if(query.equals(" "))
        {
            return treeDictionnary.get("linkedActivity").getAll();
        }
        return treeDictionnary.get("linkedActivity").getList(query, 10);
    }

    public List<String> completeTypeDocument(String query) {
        if(query.equals(" "))
        {
            return treeDictionnary.get("typeDocument").getAll();
        }
        return treeDictionnary.get("typeDocument").getList(query, 10);
    }

    public List<String> completeType(String query) {
        return treeDictionnary.get("type").getList(query, 10);
    }

    public List<String> completeStatusOfPayement(String query) {
        if(query.equals(" "))
        {
            return treeDictionnary.get("statusOfPayement").getAll();
        }
        return treeDictionnary.get("statusOfPayement").getList(query, 10);
    }

    public List<String> completeAccountName(String query) {
        if(query.equals(" "))
        {
            return treeDictionnary.get("accountName").getAll();
        }
        return treeDictionnary.get("accountName").getList(query, 10);
    }

    public List<String> completeSocietyName(String query) {
        if(query.equals(" "))
        {
            return treeDictionnary.get("societyName").getAll();
        }
        return treeDictionnary.get("societyName").getList(query, 10);
    }

    public List<String> completeCategoryItem(String query) {
        if(query.equals(" "))
        {
            return treeDictionnary.get("ItemCategory").getAll();
        }
        return treeDictionnary.get("ItemCategory").getList(query, 10);
    }

    public List<String> completeItemName(String query) {
        if(query.equals(" "))
        {
            return transactionService.getTreeDictionnaryItem(this.currentTransaction.getVendor().getSociety().getName()).getAll();
        }
        return transactionService.getTreeDictionnaryItem(this.currentTransaction.getVendor().getSociety().getName()).getList(query, 10);
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
    public void onItemForInvoiceLineSelect(SelectEvent<String> event)
    {
        InvoiceLine tplLine= transactionService.getItemDictionnaryBySociety().get(currentTransaction.getVendor().getSociety().getName()).get(event.getObject());
        InvoiceLine currentLine=(InvoiceLine)event.getComponent().getAttributes().get("currentLine");
        currentLine.getItem().setCategory(tplLine.getItem().getCategory());
        currentLine.setHTunitPrice(tplLine.getHTunitPrice());
        currentLine.getItem().setTva(tplLine.getItem().getTva());
        currentLine.getItem().setType(tplLine.getItem().getType());
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
        this.currentTransaction=transactionService.importInvoiceTransaction(documentView.getSelectDocument().getDocument());
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
    public void onRowEdit(RowEditEvent<InvoiceLine> event) {
        InvoiceLine currentLine=(InvoiceLine)event.getObject();
        if(currentLine.getQuantity()<0)
        {
            // get absolute value of quantity
            currentLine.setQuantity(Math.abs(currentLine.getQuantity()));
            // deduct the tva from the HTunitPrice
            List<Tva> tvaList=tvaRepository.findAll();
            double tauxTva=0;
            for(Tva tva:tvaList)
            {
                if(tva.getCode().equals(currentLine.getItem().getTva().getCode()))
                {
                    tauxTva=tva.getValue();
                }
            }
            if(tauxTva!=0)
            {
                currentLine.setHTunitPrice(MathUtil.round(currentLine.getHTunitPrice()/(1+tauxTva/100),2));
                if(currentLine.getHTtotalPrice()!=0)
                {
                    currentLine.setHTtotalPrice(MathUtil.round(currentLine.getHTtotalPrice()/(1+tauxTva/100),2));
                }
            }
        }
        if(currentLine.getHTtotalPrice()==0)
        {
            currentLine.setHTtotalPrice(currentLine.getHTunitPrice()*currentLine.getQuantity());
        }

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
        this.currentTransaction.getInvoiceLines().addAll(transactionService.getInvoiceLineWhitOpenAI(textForItem));
        this.calculateTotal();
    }

    public void deleteTransactionLine()
    {
        currentTransaction.getInvoiceLines().remove(selectedLine);
        this.calculateTotal();
    }

    public void saveTransaction()
    {
        PrimefaceUtil.addMessages(transactionService.saveTransaction(currentTransaction));
        this.transactions=transactionService.getAllTransactions();
    }

    public void newTransaction()
    {
        currentTransaction=new Transaction();
    }

    public void updateDictionaries()
    {
        transactionService.init();
        this.treeDictionnary=transactionService.getTreeDictionary();
    }
}
