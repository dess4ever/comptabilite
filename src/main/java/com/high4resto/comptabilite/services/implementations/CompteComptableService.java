package com.high4resto.comptabilite.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.high4resto.comptabilite.documents.CompteComptale;
import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.repository.CompteComptableRepository;
import com.high4resto.comptabilite.services.contract.CompteComptableContractService;

@Service
public class CompteComptableService implements CompteComptableContractService{

    @Autowired
    private CompteComptableRepository compteComptableRepository;

    @Override
    public List<CompteComptale> getAllCompteComptable() {
        return compteComptableRepository.findAll();
    }

    @Override
    public List<LogMessage> saveCompteComptable(CompteComptale compteComptable) {
        List<LogMessage> logs=new ArrayList<>();
        compteComptableRepository.save(compteComptable);
        LogMessage log=new LogMessage(1, "Compte comptable enregistré avec succès");
        logs.add(log);
        return logs;
    }

    @Override
    public List<LogMessage> deleteCompteComptable(CompteComptale compteComptable) {
        List<LogMessage> logs=new ArrayList<>();
        compteComptableRepository.delete(compteComptable);
        LogMessage log=new LogMessage(1, "Compte comptable supprimé avec succès");
        logs.add(log);
        return logs;
    }

    @Override
    public List<LogMessage> updateCompteComptable(CompteComptale compteComptable) {
        List<LogMessage> logs=new ArrayList<>();
        compteComptableRepository.save(compteComptable);
        LogMessage log=new LogMessage(1, "Compte comptable enregistré avec succès");
        logs.add(log);
        return logs;
    }

    @Override
    public CompteComptale searchCompteComptableWhithCode(String code) {
        return compteComptableRepository.findByCode(code);
    }
    
}
