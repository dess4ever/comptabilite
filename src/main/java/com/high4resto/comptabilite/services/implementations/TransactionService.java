package com.high4resto.comptabilite.services.implementations;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.high4resto.comptabilite.dataStruct.TreeDictionnary;
import com.high4resto.comptabilite.documents.Address;
import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.Item;
import com.high4resto.comptabilite.documents.Item_Category;
import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.documents.Rib;
import com.high4resto.comptabilite.documents.Society;
import com.high4resto.comptabilite.documents.Transaction;
import com.high4resto.comptabilite.documents.Tva;
import com.high4resto.comptabilite.documents.Vendor;
import com.high4resto.comptabilite.repository.TransactionRepository;
import com.high4resto.comptabilite.repository.TvaRepository;
import com.high4resto.comptabilite.services.contract.TransactionServiceContract;
import com.high4resto.comptabilite.utils.MathUtil;
import com.high4resto.comptabilite.utils.TextUtil;

import jakarta.annotation.PostConstruct;

@Service
public class TransactionService implements TransactionServiceContract {

    private Map<String, TreeDictionnary> treeDictionnary=new HashMap<>();
    private Map<String,Rib> ribDictionnary=new HashMap<>();
    private Map<String,Society> societyDictionnary=new HashMap<>();

    @Autowired
    private TransactionRepository transactionController;

    @Autowired
    private SirenApiService sirenApiService;

    @Autowired
    private  AiService aiService;

    @Autowired
    private TvaRepository tvaRepository;


    @PostConstruct
    public void init()
    {
        treeDictionnary.put("linkedActivity", new TreeDictionnary());
        treeDictionnary.put("type", new TreeDictionnary());
        treeDictionnary.put("typeDocument", new TreeDictionnary());
        treeDictionnary.put("statusOfPayement", new TreeDictionnary());
        treeDictionnary.put("accountName", new TreeDictionnary());
        treeDictionnary.put("accountNumber", new TreeDictionnary());
        treeDictionnary.put("societyName", new TreeDictionnary());
        treeDictionnary.put("ItemName", new TreeDictionnary());
        treeDictionnary.put("ItemCategory", new TreeDictionnary());
        Map<String, Integer> ribStrenght=new HashMap<>();
        Map<String, Integer> societyStrenght=new HashMap<>();

        for(Transaction transaction:this.getAllTransactions())
        {
            if(ribStrenght.containsKey(transaction.getVendor().getRib().getName()))
            {
                if(transaction.getVendor().getRib().getStrenght()>ribStrenght.get(transaction.getVendor().getRib().getName()))
                {
                    ribStrenght.put(transaction.getVendor().getRib().getName(), transaction.getVendor().getRib().getStrenght());
                    ribDictionnary.put(transaction.getVendor().getRib().getName(), transaction.getVendor().getRib());
                }
            }
            else
            {
                ribStrenght.put(transaction.getVendor().getRib().getName(), 1);
                ribDictionnary.put(transaction.getVendor().getRib().getName(), transaction.getVendor().getRib());
            }
            if(ribStrenght.containsKey(transaction.getOriginPayement().getName()))
            {
                if(transaction.getOriginPayement().getStrenght()>ribStrenght.get(transaction.getOriginPayement().getName()))
                {
                    ribStrenght.put(transaction.getOriginPayement().getName(), transaction.getOriginPayement().getStrenght());
                    ribDictionnary.put(transaction.getOriginPayement().getName(), transaction.getOriginPayement());
                }
            }
            else
            {
                ribStrenght.put(transaction.getOriginPayement().getName(), 1);
                ribDictionnary.put(transaction.getOriginPayement().getName(), transaction.getOriginPayement());
            }
            if(ribStrenght.containsKey(transaction.getDestinationPayement().getName()))
            {
                if(transaction.getDestinationPayement().getStrenght()>ribStrenght.get(transaction.getDestinationPayement().getName()))
                {
                    ribStrenght.put(transaction.getDestinationPayement().getName(), transaction.getDestinationPayement().getStrenght());
                    ribDictionnary.put(transaction.getDestinationPayement().getName(), transaction.getDestinationPayement());
                }
            }
            else
            {
                ribStrenght.put(transaction.getDestinationPayement().getName(), 1);
                ribDictionnary.put(transaction.getDestinationPayement().getName(), transaction.getDestinationPayement());
            }
            if(societyStrenght.containsKey(transaction.getVendor().getSociety().getName()))
            {
                if(transaction.getVendor().getSociety().getStrenght()>societyStrenght.get(transaction.getVendor().getSociety().getName()))
                {
                    societyStrenght.put(transaction.getVendor().getSociety().getName(), transaction.getVendor().getSociety().getStrenght());
                    societyDictionnary.put(transaction.getVendor().getSociety().getName(), transaction.getVendor().getSociety());
                }
            }
            else
            {
                societyStrenght.put(transaction.getVendor().getSociety().getName(), 1);
                societyDictionnary.put(transaction.getVendor().getSociety().getName(), transaction.getVendor().getSociety());
            }
            if(societyStrenght.containsKey(transaction.getCustomer().getName()))
            {
                if(transaction.getCustomer().getStrenght()>societyStrenght.get(transaction.getCustomer().getName()))
                {
                    societyStrenght.put(transaction.getCustomer().getName(), transaction.getCustomer().getStrenght());
                    societyDictionnary.put(transaction.getCustomer().getName(), transaction.getCustomer());
                }
            }
            else
            {
                societyStrenght.put(transaction.getCustomer().getName(), 1);
                societyDictionnary.put(transaction.getCustomer().getName(), transaction.getCustomer());
            }
            
            treeDictionnary.get("statusOfPayement").addWord(transaction.getPayementStatus());
            treeDictionnary.get("linkedActivity").addWord(transaction.getRefActivite());
            treeDictionnary.get("typeDocument").addWord(transaction.getTypeDocument());
            treeDictionnary.get("type").addWord(transaction.getType());
            treeDictionnary.get("accountName").addWord(transaction.getVendor().getRib().getName());
            treeDictionnary.get("accountName").addWord(transaction.getOriginPayement().getName());
            treeDictionnary.get("accountName").addWord(transaction.getDestinationPayement().getName());

            treeDictionnary.get("accountNumber").addWord(transaction.getVendor().getRib().getAccount_Number());
            treeDictionnary.get("accountNumber").addWord(transaction.getOriginPayement().getAccount_Number());
            treeDictionnary.get("accountNumber").addWord(transaction.getDestinationPayement().getAccount_Number());

            treeDictionnary.get("societyName").addWord(transaction.getVendor().getSociety().getName());
            treeDictionnary.get("societyName").addWord(transaction.getCustomer().getName());
            for(InvoiceLine line:transaction.getInvoiceLines())
            {
                treeDictionnary.get("ItemName").addWord(line.getItem().getName());
                treeDictionnary.get("ItemCategory").addWord(line.getItem().getCategory().getName());
            }
        }
    }

    public Rib getRib(String ribName)
    {
        return ribDictionnary.get(ribName);
    }

    public Society getSociety(String societyName)
    {
        return societyDictionnary.get(societyName);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionController.findAll();
    }

    @Override
    public List<LogMessage> saveTransaction(Transaction transaction) {
        List<LogMessage> logs=new ArrayList<>();
        if(transaction!=null){
            transactionController.save(transaction);
            logs.add(new LogMessage(1, "Transaction enregistrée"));
        }
        else
            logs.add(new LogMessage(2, "Transaction non enregistrée"));
        return logs;
    }

    @Override
    public List<LogMessage> deleteTransaction(Transaction transaction) {
        List<LogMessage> logs=new ArrayList<>();
        if(transaction!=null){
            transactionController.delete(transaction);
            logs.add(new LogMessage(1, "Transaction supprimée"));
        }
        else
            logs.add(new LogMessage(2, "Transaction non supprimée"));
        return logs;
    }

    @Override
    public List<LogMessage> updateTransaction(Transaction transaction) {
        List<LogMessage> logs=new ArrayList<>();
        if(transaction!=null){
            transactionController.save(transaction);
            logs.add(new LogMessage(1, "Transaction modifiée"));
        }
        else
            logs.add(new LogMessage(2, "Transaction non modifiée"));
        return logs;
    }

    @Override
    public Map<String, TreeDictionnary> getTreeDictionary() {
        return treeDictionnary;
    }

    private Transaction importMetroTransaction(String pdf)
    {
        Transaction transaction = new Transaction();
        Vendor vendor = new Vendor();
        Society society = new Society();
        Address address = new Address();
        List<InvoiceLine> transactionLine = new ArrayList<>();
        Pattern pattern;
        Matcher matcher;

        society.setAddress(address);
        vendor.setSociety(society);
        transaction.setVendor(vendor);

        int beginDate;
        beginDate = pdf.indexOf("Date facture");
        pattern = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
        String date = pdf.substring(beginDate, beginDate + 50);
        matcher = pattern.matcher(date);
        if (matcher.find())
            try {
                transaction.setInvoiceDate(TextUtil.getDateFromString(matcher.group()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        beginDate = pdf.indexOf("REFLEXE PLUS en cours de validité");
        pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{2}.{3}[0-9]{2}:[0-9]{2}:[0-9]{2}");
        date = pdf.substring(beginDate);
        matcher = pattern.matcher(date);
        if (matcher.find())
            try {
                transaction.setPayementDate(TextUtil.getDateFromString(matcher.group()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        pattern = Pattern.compile("SIRET:[0-9]{3}.[0-9]{3}.[0-9]{3}.[0-9]{2}.[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()){
            String siret=matcher.group().substring(6).replaceAll("\s+", "");
            society=sirenApiService.getVendorFromSiret(siret).getSociety();
            transaction.getVendor().setSociety(society);
        }

        pattern = Pattern.compile("FR.[0-9]{2}.[0-9]{3}.[0-9]{3}.CE");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
           transaction.getVendor().setApproval(matcher.group().trim());

        pattern = Pattern.compile("R.C..[A-Z][a-z]{2,}.[A-B].[0-9]{3}.[0-9]{3}.[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
            transaction.getVendor().getSociety().setRcs(matcher.group().substring(5));

        pattern = Pattern.compile("TEL.:[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{2}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
        transaction.getVendor().getSociety().setPhone(matcher.group().substring(5));

        pattern = Pattern.compile(
                "[0-9]\\D{1,}[0-9]\\D{1,}[0-9]{3}\\D{1,}[0-9]{4}\\D{1,}[0-9]{6}②.{1,}[0-9]{3}/[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            String result = matcher.group();
            transaction.setNumber(result.replaceAll("\s+", ""));
        }

        List<Integer> indexOfSubcategory = new ArrayList<>();
        int indexSub = pdf.indexOf("*** ");
        while (indexSub >= 0) {
            indexOfSubcategory.add(indexSub);
            indexSub = pdf.indexOf("*** ", indexSub + 1);
        }

        pattern = Pattern.compile("(\n\u0020{3}[0-9]{8,14}\\D{1,}[0-9]{6})|(\n)M\s+[0-9]{7}");
        int currenIndex = 0;
        for (Integer begin : indexOfSubcategory) {
            int end = pdf.indexOf("Total", begin + 1);
            String subCategoryName = pdf.substring(begin + 4, end - 1);
            String currentT = pdf.substring(currenIndex, begin);
            matcher = pattern.matcher(currentT);

            while (matcher.find() && matcher.end() < begin) {
                int lenght=matcher.group().length()+2;
                Tva tpTva = new Tva();
                InvoiceLine tpLine = new InvoiceLine();
                Item tpItem = new Item();
                Item_Category category = new Item_Category();
                int beginItem = matcher.start();
                int endItem = currentT.indexOf("\n", beginItem + lenght);
                String[] splited = currentT.substring(beginItem + lenght, endItem).split("[\\s]{2,}");
                tpItem.setFact_Name(splited[0]);
                category.setName(subCategoryName);
                tpItem.setCategory(category);
                int tpIndex = 0;
                for (String s : splited) {
                    if (s.trim().matches("[ABCD]") && tpIndex > 3) {
                        tpTva.setCode(s.trim());
                        tpItem.setTva(tpTva);
                        tpLine.setHTtotalPrice(Double.parseDouble(splited[tpIndex - 1].strip()
                                .replace(',', '.').replace('\u00A0', ' ').trim()));
                        tpLine.setHTunitPrice(Double.parseDouble(splited[tpIndex - 3].strip()
                                .replace(',', '.').replace('\u00A0', ' ').trim()));
                        String tpQTY = splited[tpIndex - 2];
                        String[] tpQTYs = tpQTY.split("\u00A0");
                        if (tpQTYs.length > 1)
                            tpLine.setQuantity(Integer.parseInt(tpQTYs[0].strip())
                                    * Integer.parseInt(tpQTYs[1].strip()));
                        else
                            tpLine.setQuantity(Integer.parseInt(tpQTY.replace('\u00A0', ' ').trim()));
                    }
                    tpIndex += 1;
                }
                tpLine.setItem(tpItem);
                transactionLine.add(tpLine);
                transaction.setInvoiceLines(transactionLine);

            }
            
            currenIndex = begin;
        }
        // Calcul des cotisations sociales
        double montantCotis=0;
        int index = pdf.indexOf("Plus : COTIS. SECURITE SOCIALE");
        while (index >= 0) {
            pattern = Pattern.compile("[0-9]{1,},[0-9]{2}");
            matcher = pattern.matcher(pdf.substring(index));
            if (matcher.find())
            {
                String montant=matcher.group().replace(",", ".");
                montantCotis+=Double.parseDouble(montant);
            }
            index = pdf.indexOf("Plus : COTIS. SECURITE SOCIALE", index + 1);

        }
        if(montantCotis>0)
        {
            InvoiceLine tpLine = new InvoiceLine();
            Item tpItem = new Item();
            Item_Category category = new Item_Category();
            Tva tpTva = new Tva();
            tpTva.setCode("D");
            tpItem.setTva(tpTva);
            category.setName("Cotisations sociales");
            tpItem.setCategory(category);
            tpItem.setFact_Name("Cotisations sociales");
            tpLine.setHTtotalPrice(MathUtil.round(montantCotis, 2));
            tpLine.setHTunitPrice(MathUtil.round(montantCotis, 2));
            tpLine.setQuantity(1);
            tpLine.setItem(tpItem);
            transactionLine.add(tpLine);            
        }

        transaction.setType("Achat");
        transaction.setTypeDocument("Facture");
        return transaction;
    }

    private static Transaction importColichef(String pdf)
    {
        Matcher matcher;
        Pattern pattern;
        Transaction transaction = new Transaction();
        // Get transaction number
        pattern = Pattern.compile("#FA[0-9]{6}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            String result = matcher.group();
            transaction.setNumber(result.replaceAll("\s+", ""));
        }

        // Set address
        transaction.getVendor().getSociety().setName("Colichef");
        transaction.getVendor().getSociety().getAddress().setName("34 Rue de la Cale Sèche 4684 Haccourt Belgique");
        transaction.getVendor().getSociety().setTva_Number("BE 0476.778.922");
        
        // Get transaction date
        pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            String result = matcher.group();
            try {
                transaction.setInvoiceDate(TextUtil.getDateFromString(result));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        transaction.setType("Achat");
        transaction.setTypeDocument("Facture");
        return transaction;
    }

    // Convert pdf invoice to string
    @Override
    public Transaction importTransaction(String content) {
        System.out.println("Importing invoice");
        System.out.println(content);
		try {
			if (content.contains("METRO France")) {
                return importMetroTransaction(content);
			}
            else if (content.contains("Colichef"))
            {
                return importColichef(content);
            }
            else 
            {
                //search Siret number
                Transaction transaction = new Transaction();
                Pattern pattern = Pattern.compile("\\W{1,}[0-9]{14}\\W{1,}");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    String result = matcher.group();
                    pattern = Pattern.compile("[0-9]{14}");
                    matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        result = matcher.group();
                    }
                    transaction.setVendor(sirenApiService.getVendorFromSiret(result));
                    transaction.setType("Achat");
                    transaction.setTypeDocument("Facture");
                    return transaction;
                }
            }
            
            return new Transaction();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    @Override
    public Transaction calculateTotal(Transaction newTransaction)
    {
        double totalHT = 0;
        double totalTTC = 0;
        double totalTVA = 0;
        double totalTVA_A = 0;
        double totalTVA_B = 0;
        double totalTVA_C = 0;
        double totalTVA_D = 0;

        List<Tva> tvaList = tvaRepository.findAll();
        double aTva = tvaList.get(0).getValue();
        double bTva = tvaList.get(1).getValue();
        double cTva = tvaList.get(2).getValue();
        double dTva = tvaList.get(3).getValue();

        int size=newTransaction.getInvoiceLines().size();
        for (int i = 0; i < size; i++) {
            totalHT+=newTransaction.getInvoiceLines().get(i).getHTtotalPrice();
            if(newTransaction.getInvoiceLines().get(i).getItem().getTva().getCode().equals("A"))
            {
                double value=newTransaction.getInvoiceLines().get(i).getHTtotalPrice()*(100+aTva)/100;
                totalTTC+=value;
                totalTVA_A+=value-newTransaction.getInvoiceLines().get(i).getHTtotalPrice();

            }
            else if(newTransaction.getInvoiceLines().get(i).getItem().getTva().getCode().equals("B"))
            {
                double value=newTransaction.getInvoiceLines().get(i).getHTtotalPrice()*(100+bTva)/100;
                totalTTC+=value;
                totalTVA_B+=value-newTransaction.getInvoiceLines().get(i).getHTtotalPrice();
            }
            else if(newTransaction.getInvoiceLines().get(i).getItem().getTva().getCode().equals("C"))
            {
                double value=newTransaction.getInvoiceLines().get(i).getHTtotalPrice()*(100+cTva)/100;
                totalTTC+=value;
                totalTVA_C+=value-newTransaction.getInvoiceLines().get(i).getHTtotalPrice();

            }
            else if(newTransaction.getInvoiceLines().get(i).getItem().getTva().getCode().equals("D"))
            {
                double value=newTransaction.getInvoiceLines().get(i).getHTtotalPrice()*(100+dTva)/100;
                totalTTC+=value;
                totalTVA_D+=value-newTransaction.getInvoiceLines().get(i).getHTtotalPrice();
            }
            else
            {
                totalTTC+=newTransaction.getInvoiceLines().get(i).getHTtotalPrice();
            }
        }
        totalHT=MathUtil.round(totalHT, 2);
        totalTTC=MathUtil.round(totalTTC, 2);
        totalTVA=MathUtil.round(totalTTC-totalHT,2);
        totalTVA_A=MathUtil.round(totalTVA_A,2);
        totalTVA_B=MathUtil.round(totalTVA_B,2);
        totalTVA_C=MathUtil.round(totalTVA_C,2);
        totalTVA_D=MathUtil.round(totalTVA_D,2);
        newTransaction.setTotalHT(totalHT);
        newTransaction.setTotalTTC(totalTTC);
        newTransaction.setTotalTVA(totalTVA);
        newTransaction.setTotalTVAB(totalTVA_B);
        newTransaction.setTotalTVAC(totalTVA_C);
        newTransaction.setTotalTVAD(totalTVA_D);
        return newTransaction;
    }

       // Get invoice line whit OpenAI
	public List<InvoiceLine> getInvoiceLineWhitOpenAI(String text)
	{
		List<InvoiceLine> lines=new ArrayList<InvoiceLine>();
		text+="\n|Produit ou service|Quantité|Prix unitaire|Prix total|\n";
		text+="|---|---|---|---|\n";
		String reponse=aiService.request(text);
		String[] linesReponse=reponse.split("\n");
        Pattern pattern = Pattern.compile("(\\d+(?:[\\.\\,]\\d{2})?)");
        Matcher matcher;

        for(String line:linesReponse)
		{
			if(line.contains("|"))
			{
				String[] lineSplit=line.split("\\|");
				if(lineSplit.length==5)
				{
                        if(lineSplit[1].length()>=1&&lineSplit[2].length()>=1&&lineSplit[3].length()>=1&&lineSplit[4].length()>=1)
                        {
                        InvoiceLine invoiceLine=new InvoiceLine();
                        invoiceLine.getItem().setFact_Name(lineSplit[1]);
                        try
                        {
                            invoiceLine.setQuantity(Integer.parseInt(lineSplit[2]));
                        }
                        catch(Exception e)
                        {
                            invoiceLine.setQuantity(1);
                        }                        
                        String htUnitprice=lineSplit[3].replace(",", ".");
                        String htTotalprice=lineSplit[4].replace(",", ".");
                        htTotalprice.replaceAll("\\s+", "");
                        htUnitprice.replaceAll("\\s+", "");
                        matcher = pattern.matcher(htUnitprice);
                        if (matcher.find())
                        {
                            htUnitprice=matcher.group(1);
                        }
                        matcher = pattern.matcher(htTotalprice);
                        if (matcher.find())
                        {
                            htTotalprice=matcher.group(1);
                        }

                        if (htUnitprice.length()>=1)
                        try
                        {
                            invoiceLine.setHTunitPrice(Double.parseDouble(htUnitprice));
                        }
                        catch(Exception e)
                        {
                            invoiceLine.setHTunitPrice(0);
                        }
                        if (htTotalprice.length()>=1)
                        try
                        {
                            invoiceLine.setHTtotalPrice(Double.parseDouble(htTotalprice));
                        }
                        catch(Exception e)
                        {
                            invoiceLine.setHTtotalPrice(0);
                        }
                        invoiceLine.getItem().setTva(new Tva("I", 20.0,""));
                        lines.add(invoiceLine);
                    }
				}
			}
		}
		return lines;
	}
    
}
