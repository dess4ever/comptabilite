package com.high4resto.comptabilite;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.high4resto.comptabilite.documents.StockItem;
import com.high4resto.comptabilite.documents.VendorInvoice;
import com.high4resto.comptabilite.repository.VendorInvoiceRepository;
import com.high4resto.comptabilite.utils.InvoiceUtil;
import com.high4resto.comptabilite.utils.MathUtil;

@SpringBootApplication
public class ComptabiliteApplication implements CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(ComptabiliteApplication.class);
	@Autowired
	private VendorInvoiceRepository vendorInvoiceRepository;

	public static void main(String[] args) {
		SpringApplication.run(ComptabiliteApplication.class, args);
	}

	// open ip adrr show eth0
	private String getIp() {
		String ip = "";
		try {
			Process p = Runtime.getRuntime().exec("ip addr show eth0");
			p.waitFor();
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.contains("inet ")) {
					ip = line.split("inet ")[1].split("/")[0];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

			return ip;
	}
	public void getAlcoolFromInvoice()
	{
		List<String> categoriToUse=new ArrayList<String>();
		categoriToUse.add("CAVE");
		categoriToUse.add("CHAMPAGNES");
		categoriToUse.add("Cotisations sociales");
		categoriToUse.add("BRASSERIE");
		categoriToUse.add("SPIRITUEUX");
			

		List<VendorInvoice> invoices=vendorInvoiceRepository.findAll();
		List<StockItem> items=new ArrayList<StockItem>();

		for (VendorInvoice vendorInvoice : invoices) {
			vendorInvoice=InvoiceUtil.calculateTotal(vendorInvoice, 2.2, 5.5, 10.0, 20.0);

			vendorInvoice.getInvoiceLines().forEach(line->{
				if(categoriToUse.contains(line.getItem().getCategory().getName()))
				{
					if(line.getItem().getTva().getCode().equals("D"))
					{
						// Est ce que l'item est déjà dans la liste
						boolean found=false;
						for (StockItem stockItem : items) {
							if(stockItem.getItem().getFact_Name().equals(line.getItem().getFact_Name()))
							{
								// On ajoute la quantité
								stockItem.setQuantity(stockItem.getQuantity()+line.getQuantity());
								// On ajoute le prix
								stockItem.setPrice(stockItem.getPrice()+line.getHTtotalPrice());
								found=true;
							}
						}
						if(!found)
						{
							// Sinon on ajoute l'item
							items.add(new StockItem(line.getQuantity(), line.getItem().getFact_Name(), line.getItem(),line.getHTtotalPrice()));
						}

					}
				}

			});
		}
		for(StockItem item:items)
		{
			System.out.println(item.getQuantity()+";"+item.getName()+";"+MathUtil.round(item.getPrice(), 2));
		}
	}

	public void totalMetro()
	{
		List<VendorInvoice> invoices=vendorInvoiceRepository.findAll();
		double totalHT=0;
		double totalTTC=0;
		double totalTVA=0;
		double totalTVAB=0;
		double totalTVAC=0;
		double totalTVAD=0;
		for(VendorInvoice invoice:invoices)
		{
			if(invoice.getIssuer().getSociety().getName().equals("METRO FRANCE"))
			{
				invoice=InvoiceUtil.calculateTotal(invoice, 2.2, 5.5, 10.0, 20.0);
				totalHT+=invoice.getTotalHT();
				totalTTC+=invoice.getTotalTTC();
				totalTVA+=invoice.getTotalTVA();
				totalTVAB+=invoice.getTotalTVA55();
				totalTVAC+=invoice.getTotalTVA10();
				totalTVAD+=invoice.getTotalTVA20();	
			}
		}
		System.out.println("Total HT: "+MathUtil.round(totalHT, 2));
		System.out.println("Total TVA: "+MathUtil.round(totalTVA, 2));
		System.out.println("Total TVA 5.5%: "+MathUtil.round(totalTVAB, 2));
		System.out.println("Total TVA 10%: "+MathUtil.round(totalTVAC, 2));
		System.out.println("Total TVA 20%: "+MathUtil.round(totalTVAD, 2));
		System.out.println("Total TTC: "+MathUtil.round(totalTTC, 2));		
	}
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("Bienvenu sur mon logiciel de comptabilité");		
		logger.info("Pour accéder à l'interface: http://" + getIp()+":8080"+"/index.xhtml");
	}

}
