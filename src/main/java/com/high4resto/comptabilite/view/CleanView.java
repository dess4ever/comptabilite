package com.high4resto.comptabilite.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.services.implementations.TransactionService;
import com.high4resto.comptabilite.utils.PrimefaceUtil;

@Component("cleanView")
@SessionScope
public class CleanView {
    @Autowired
    private TransactionService transactionService;

    public void cleanTva() {
      transactionService.cleanTvaCode();
      PrimefaceUtil.info("TVA associées aux transactions");
   } 
}
