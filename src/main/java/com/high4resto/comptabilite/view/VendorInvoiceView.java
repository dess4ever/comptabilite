package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.dataStruct.TreeDictionnary;
import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.Tva;
import com.high4resto.comptabilite.documents.Vendor;
import com.high4resto.comptabilite.documents.VendorInvoice;
import com.high4resto.comptabilite.repository.TvaRepository;
import com.high4resto.comptabilite.repository.VendorInvoiceRepository;
import com.high4resto.comptabilite.utils.InvoiceUtil;
import com.high4resto.comptabilite.utils.PrimefaceUtil;
import com.high4resto.comptabilite.utils.SirenApiUtil;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component("invoice")
@SessionScope
public class VendorInvoiceView implements Serializable{
    @Getter @Setter
     VendorInvoice newInvoice=new VendorInvoice();

    @Getter @Setter
    private Double totalTTC=0.0;
    @Getter @Setter
    private Double totalHT=0.0;
    @Getter @Setter
    private Double totalTVA=0.0;

    @Autowired
    private FileUploadView document;
    @Autowired
    private UserLoginView userLoginView;
    @Autowired
    private TvaRepository tvaRepository;
    @Autowired
    private VendorInvoiceRepository vendorInvoiceRepository;

    @Getter @Setter
    private InvoiceLine selectedLine;

    @Getter @Setter
    private VendorInvoice selectedInvoice;
    
    @Getter @Setter
    private List<VendorInvoice> vendorInvoices=new ArrayList<VendorInvoice>();
    
    @Getter @Setter
    private String textForItem;
    
    private List<Tva> tvas=new ArrayList<Tva>();
    private  TreeDictionnary categoryPrediction=new TreeDictionnary();
    
    @PostConstruct
    public void init() {
        this.tvas=tvaRepository.findAll();
        this.vendorInvoices=vendorInvoiceRepository.findAll();
        for(VendorInvoice invoice:this.vendorInvoices)
        {
            for(InvoiceLine line:invoice.getInvoiceLines())
            {
                String category=line.getItem().getCategory().getName();
                categoryPrediction.addWord(category);
            }
        }
    }

    // Calculate the total of the invoice
    private void calculateTotal()
    {

        double aTva=tvas.get(0).getValue();
        double bTva=tvas.get(1).getValue();
        double cTva=tvas.get(2).getValue();
        double dTva=tvas.get(3).getValue();
        this.newInvoice= InvoiceUtil.calculateTotal(this.newInvoice, aTva, bTva, cTva, dTva);

        this.totalHT=this.newInvoice.getTotalHT();
        this.totalTTC=this.newInvoice.getTotalTTC();
        this.totalTVA=this.newInvoice.getTotalTVA();
    }

    public void recalculateTotal()
    {
        this.calculateTotal();
    }
    
    public void importInvoice()
    {
        if(this.document.getSelectDocument()!=null)
        {
            String textForItem=this.document.getSelectDocument().getBrut();
            this.newInvoice=InvoiceUtil.importInvoice(this.document.getSelectDocument().getBrut());
            this.newInvoice.setRefDocument(this.document.getSelectDocument().getHash());
            this.calculateTotal();
            this.textForItem=textForItem;   
        }
    }

    public void newVendorInvoice()
    {
        this.newInvoice=new VendorInvoice();
    }
    public void setMyRib()
    {
        this.newInvoice.setOriginPayement(userLoginView.getUser().getRib());
    }
    public void addInvoiceLine()
    {
        this.newInvoice.getInvoiceLines().add(new InvoiceLine());
    }
    public void deleteInvoiceLine()
    {
        this.newInvoice.getInvoiceLines().remove(this.selectedLine);
        this.calculateTotal();
    }
    public void deleteInvoice()
    {
        this.vendorInvoiceRepository.delete(this.selectedInvoice);
        this.vendorInvoices.remove(this.selectedInvoice);
        PrimefaceUtil.warn("Facture supprimée");
    }
    public void searchFromSiret()
    {
        // Verify if the SIRET is valid with regex
        if(SirenApiUtil.isSiretValid(this.newInvoice.getIssuer().getSociety().getSiret()))
        {
            // Call the API to get the vendor
            Vendor tpVendor=SirenApiUtil.getVendorFromSiret(this.newInvoice.getIssuer().getSociety().getSiret());
            this.newInvoice.getIssuer().getSociety().setSiren(tpVendor.getSociety().getSiren());
            this.newInvoice.getIssuer().getSociety().setName(tpVendor.getSociety().getName());;
            this.newInvoice.getIssuer().getSociety().setAddress(tpVendor.getSociety().getAddress());
            this.newInvoice.getIssuer().getSociety().setApe(tpVendor.getSociety().getApe());
            this.newInvoice.getIssuer().getSociety().setTva_Number(tpVendor.getSociety().getTva_Number());
        }
        else
        {
            // Display an error message
            PrimefaceUtil.error("Le SIRET n'est pas valide"); 
        }
    }
    // Verify if the invoice is valid
    private boolean verifyInvoice()
    {
        boolean result=true;
        if(this.newInvoice.getIssuer().getSociety().getName()==null || this.newInvoice.getIssuer().getSociety().getName().equals(""))
        {
            PrimefaceUtil.error("Le nom de la société est obligatoire");
        }
        else if(this.newInvoice.getIssuer().getSociety().getAddress()==null || this.newInvoice.getIssuer().getSociety().getAddress().equals(""))
        {
            PrimefaceUtil.error("L'adresse de la société est obligatoire");
        }
        return result;
    }
    public void saveInvoice()
    {
        if(verifyInvoice())
        {
            vendorInvoiceRepository.save(this.newInvoice);
            PrimefaceUtil.info("La facture a été enregistrée");
            this.vendorInvoices=vendorInvoiceRepository.findAll();
        }
        
    }

    // Save the modification of the invoice
    public void onRowEdit(RowEditEvent<VendorInvoice> event) {
        this.calculateTotal();
    }

    // Cancel the modification of the invoice
    public void onRowCancel(RowEditEvent<VendorInvoice> event) {
        PrimefaceUtil.info("Modification annulée");
    }

    public void onRowSelect(SelectEvent<VendorInvoice> event) {
        this.newInvoice=event.getObject();
        this.calculateTotal();
    }

    // Get the list of category suggestion
    public List<String> categorySuggestions(String enteredValue) {
        enteredValue = enteredValue.toLowerCase();
        List<String> matches = this.categoryPrediction.getList(enteredValue, 10);
        matches.add("Non déterminé");
        return matches;
    }

    public void openAiInvoiceLineAnalizer()
    {
        //add line to current invoice
        this.newInvoice.getInvoiceLines().addAll(InvoiceUtil.getInvoiceLineWhitOpenAI(this.textForItem));
    }
}
