package com.high4resto.comptabilite.utils;

import java.io.IOException;
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

public class InvoiceUtil {
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
        if (matcher.find())
            society.setSiret(matcher.group().substring(6).trim());

        pattern = Pattern.compile("METRO [A-Z]{1,}");
        matcher = pattern.matcher(pdf);
        if (matcher.find()) {
            matcher.find();
            society.setName(matcher.group());
        }

        pattern = Pattern.compile("FR.[0-9]{2}.[0-9]{3}.[0-9]{3}.CE");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
            vendor.setApproval(matcher.group().trim());

        pattern = Pattern.compile("comm.:.FR[1-9]{11}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
            society.setTva_Number(matcher.group().substring(7));

        pattern = Pattern.compile("R.C..[A-Z][a-z]{2,}.[A-B].[0-9]{3}.[0-9]{3}.[0-9]{3}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
            society.setRcs(matcher.group());

        pattern = Pattern.compile("TEL.:[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{2}.[0-9]{2}");
        matcher = pattern.matcher(pdf);
        if (matcher.find())
            society.setPhone(matcher.group().substring(5));

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

        pattern = Pattern.compile("[0-9]{13}\\D{1,}[0-9]{6}");
        int currenIndex = 0;
        for (Integer begin : indexOfSubcategory) {
            int end = pdf.indexOf("Total", begin + 1);
            String subCategoryName = pdf.substring(begin + 4, end - 1);
            String currentT = pdf.substring(currenIndex, begin);
            matcher = pattern.matcher(currentT);

            while (matcher.find() && matcher.end() < begin) {

                Tva tpTva = new Tva();
                InvoiceLine tpLine = new InvoiceLine();
                Item tpItem = new Item();
                Item_Category category = new Item_Category();
                int beginItem = matcher.start();
                int endItem = currentT.indexOf("\n", beginItem + 23);
                String[] splited = currentT.substring(beginItem + 23, endItem).split("[\\s]{2,}");
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
        return invoice;
    }
    public static VendorInvoice importInvoice(byte[] content) {
		try {
			String pdf = TextUtil.getTextFromPDF(content);
			if (pdf.contains("METRO France")) {
                return importMetroInvoice(pdf);
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
