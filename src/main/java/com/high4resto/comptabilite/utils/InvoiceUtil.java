package com.high4resto.comptabilite.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.high4resto.comptabilite.documents.Address;
import com.high4resto.comptabilite.documents.InvoiceLine;
import com.high4resto.comptabilite.documents.Item;
import com.high4resto.comptabilite.documents.Item_Category;
import com.high4resto.comptabilite.documents.Society;
import com.high4resto.comptabilite.documents.Tva;
import com.high4resto.comptabilite.documents.Vendor;
import com.high4resto.comptabilite.documents.VendorInvoice;

import ch.qos.logback.core.joran.conditional.ElseAction;

public class InvoiceUtil {

    // Get invoice line whit OpenAI
	public static List<InvoiceLine> getInvoiceLineWhitOpenAI(String text)
	{
		List<InvoiceLine> lines=new ArrayList<InvoiceLine>();
		OpenAiUtil openAiUtil=new OpenAiUtil();
		text+="\n|Produit ou service|Quantité|Prix unitaire|Prix total|\n";
		text+="|---|---|---|---|\n";
		String reponse=openAiUtil.request(text);
		String[] linesReponse=reponse.split("\n");
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
                        Pattern pattern = Pattern.compile("(\\d+(?:[\\.\\,]\\d{2})?)");
                        Matcher matcher = pattern.matcher(htUnitprice);
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
    // Convert a pdf from Metro invoice to VendorInvoice object
    private static VendorInvoice importMetroInvoice(String pdf)
    {
        VendorInvoice invoice = new VendorInvoice();
        Vendor vendor = new Vendor();
        Society society = new Society();
        Address address = new Address();
        List<InvoiceLine> invoiceLine = new ArrayList<>();
        Pattern pattern;
        Matcher matcher;

        society.setAddress(address);
        vendor.setSociety(society);
        invoice.setIssuer(vendor);

        int beginDate;
        beginDate = pdf.indexOf("Date facture");
        pattern = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
        String date = pdf.substring(beginDate, beginDate + 50);
        matcher = pattern.matcher(date);
        if (matcher.find())
            invoice.setDate_Of_Invoice(matcher.group());

        beginDate = pdf.indexOf("REFLEXE PLUS en cours de validité");
        pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{2}.{3}[0-9]{2}:[0-9]{2}:[0-9]{2}");
        date = pdf.substring(beginDate);
        matcher = pattern.matcher(date);
        if (matcher.find())
            invoice.setDate_Of_Payment(matcher.group());

        pattern = Pattern.compile("SIRET:[0-9]{3}.[0-9]{3}.[0-9]{3}.[0-9]{2}.[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()){
            String siret=matcher.group().substring(6).replaceAll("\s+", "");
            society=SirenApiUtil.getVendorFromSiret(siret).getSociety();
            invoice.getIssuer().setSociety(society);

        }

        pattern = Pattern.compile("FR.[0-9]{2}.[0-9]{3}.[0-9]{3}.CE");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
           invoice.getIssuer().setApproval(matcher.group().trim());

        pattern = Pattern.compile("R.C..[A-Z][a-z]{2,}.[A-B].[0-9]{3}.[0-9]{3}.[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
            invoice.getIssuer().getSociety().setRcs(matcher.group().substring(5));

        pattern = Pattern.compile("TEL.:[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{2}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
        invoice.getIssuer().getSociety().setPhone(matcher.group().substring(5));

        pattern = Pattern.compile(
                "[0-9]\\D{1,}[0-9]\\D{1,}[0-9]{3}\\D{1,}[0-9]{4}\\D{1,}[0-9]{6}②.{1,}[0-9]{3}/[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            String result = matcher.group();
            invoice.setNumber(result.replaceAll("\s+", ""));
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
                invoiceLine.add(tpLine);
                invoice.setInvoiceLines(invoiceLine);

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
            invoiceLine.add(tpLine);            
        }


        return invoice;
    }

    private static VendorInvoice importColichef(String pdf)
    {
        Matcher matcher;
        Pattern pattern;
        VendorInvoice invoice = new VendorInvoice();
        // Get invoice number
        pattern = Pattern.compile("#FA[0-9]{6}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            String result = matcher.group();
            invoice.setNumber(result.replaceAll("\s+", ""));
        }

        // Set address
        invoice.getIssuer().getSociety().setName("Colichef");
        invoice.getIssuer().getSociety().getAddress().setName("34 Rue de la Cale Sèche 4684 Haccourt Belgique");
        invoice.getIssuer().getSociety().setTva_Number("BE 0476.778.922");
        
        // Get invoice date
        pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            String result = matcher.group();
            invoice.setDate_Of_Invoice(result);
        }
        return invoice;
    } 

    // Convert pdf invoice to string
    public static VendorInvoice importInvoice(String content) {
        System.out.println("Importing invoice");
        System.out.println(content);
		try {
			if (content.contains("METRO France")) {
                return importMetroInvoice(content);
			}
            else if (content.contains("Colichef"))
            {
                return importColichef(content);
            }
            else 
            {
                //search Siret number
                VendorInvoice invoice = new VendorInvoice();
                Pattern pattern = Pattern.compile("\\W{1,}[0-9]{14}\\W{1,}");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    String result = matcher.group();
                    pattern = Pattern.compile("[0-9]{14}");
                    matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        result = matcher.group();
                    }
                    invoice.setIssuer(SirenApiUtil.getVendorFromSiret(result));
                    return invoice;
                }
            }
            return new VendorInvoice();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

    public static VendorInvoice calculateTotal(VendorInvoice newInvoice, double aTva, double bTva, double cTva, double dTva)
    {
        double totalHT = 0;
        double totalTTC = 0;
        double totalTVA = 0;
        double totalTVA_A = 0;
        double totalTVA_B = 0;
        double totalTVA_C = 0;
        double totalTVA_D = 0;


        int size=newInvoice.getInvoiceLines().size();
        for (int i = 0; i < size; i++) {
            totalHT+=newInvoice.getInvoiceLines().get(i).getHTtotalPrice();
            if(newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("A"))
            {
                double value=newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+aTva)/100;
                totalTTC+=value;
                totalTVA_A+=value-newInvoice.getInvoiceLines().get(i).getHTtotalPrice();

            }
            else if(newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("B"))
            {
                double value=newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+bTva)/100;
                totalTTC+=value;
                totalTVA_B+=value-newInvoice.getInvoiceLines().get(i).getHTtotalPrice();
            }
            else if(newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("C"))
            {
                double value=newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+cTva)/100;
                totalTTC+=value;
                totalTVA_C+=value-newInvoice.getInvoiceLines().get(i).getHTtotalPrice();

            }
            else if(newInvoice.getInvoiceLines().get(i).getItem().getTva().getCode().equals("D"))
            {
                double value=newInvoice.getInvoiceLines().get(i).getHTtotalPrice()*(100+dTva)/100;
                totalTTC+=value;
                totalTVA_D+=value-newInvoice.getInvoiceLines().get(i).getHTtotalPrice();
            }
            else
            {
                totalTTC+=newInvoice.getInvoiceLines().get(i).getHTtotalPrice();
            }
        }
        totalHT=MathUtil.round(totalHT, 2);
        totalTTC=MathUtil.round(totalTTC, 2);
        totalTVA=MathUtil.round(totalTTC-totalHT,2);
        totalTVA_A=MathUtil.round(totalTVA_A,2);
        totalTVA_B=MathUtil.round(totalTVA_B,2);
        totalTVA_C=MathUtil.round(totalTVA_C,2);
        totalTVA_D=MathUtil.round(totalTVA_D,2);
        newInvoice.setTotalHT(totalHT);
        newInvoice.setTotalTTC(totalTTC);
        newInvoice.setTotalTVA(totalTVA);
        newInvoice.setTotalTVA55(totalTVA_B);
        newInvoice.setTotalTVA10(totalTVA_C);
        newInvoice.setTotalTVA20(totalTVA_D);
        return newInvoice;
    }
}
