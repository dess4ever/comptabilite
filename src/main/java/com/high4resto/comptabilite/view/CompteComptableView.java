package com.high4resto.comptabilite.view;

import java.util.List;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.dataStruct.TreeDictionnary;
import com.high4resto.comptabilite.documents.CompteComptale;
import com.high4resto.comptabilite.services.implementations.CompteComptableService;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component("compteComptableView")
@SessionScope
public class CompteComptableView {
    @Autowired
    private CompteComptableService compteComptableService;

    @Getter @Setter
    private List<CompteComptale> compteComptales;

    @Getter @Setter
    private TreeDictionnary dictionnary;

    @Getter @Setter
    private String searchInput;

    @Getter @Setter
    private String description="";

    @Getter @Setter
    private String casReel="";

    @PostConstruct
    public void init() {
        dictionnary = new TreeDictionnary();
        this.compteComptales = compteComptableService.getAllCompteComptable();
        for(CompteComptale compteComptale : compteComptales) {
            String mot=compteComptale.getCode()+":"+compteComptale.getLibelle();
            dictionnary.addWord(mot.toLowerCase(), mot, 100/compteComptale.getCode().length());
        }
    }

    public List<String> completeCode(String query) {
        if(query.equals(" "))
            return dictionnary.getAll();
        return dictionnary.getList(query,10);
    }

    public void onItemSelect(SelectEvent<String> event) {
        String[] split = event.getObject().split(":");
        CompteComptale tp=this.compteComptableService.searchCompteComptableWhithCode(split[0]);
        this.description=tp.getDescription();
        this.casReel=tp.getExample();
    }
}
