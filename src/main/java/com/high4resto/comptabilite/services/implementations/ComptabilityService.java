package com.high4resto.comptabilite.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.high4resto.comptabilite.documents.ComptabilityRules;
import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.repository.ComptabilityRulesRepository;
import com.high4resto.comptabilite.services.contract.ComptabilityRulesContract;

public class ComptabilityService implements ComptabilityRulesContract {

    @Autowired
    private ComptabilityRulesRepository comptabilityRulesRepository;

    @Override
    public List<ComptabilityRules> getAllComptabilityRules() {
        return comptabilityRulesRepository.findAll();
    }

    @Override
    public List<LogMessage> saveComptabilityRules(ComptabilityRules comptabilityRules) {
        List<LogMessage> logs=new ArrayList<>();
        comptabilityRulesRepository.save(comptabilityRules);
        LogMessage log=new LogMessage(1, "Rêgle Comptable enregistrée avec succès");
        logs.add(log);
        return logs;
    }

    @Override
    public List<LogMessage> deleteComptabilityRules(ComptabilityRules comptabilityRules) {
        List<LogMessage> logs=new ArrayList<>();
        comptabilityRulesRepository.delete(comptabilityRules);
        LogMessage log=new LogMessage(1, "Rêgle Comptable supprimée avec succès");
        logs.add(log);
        return logs;
    }

    @Override
    public List<LogMessage> updateComptabilityRules(ComptabilityRules comptabilityRules) {
        List<LogMessage> logs=new ArrayList<>();
        comptabilityRulesRepository.save(comptabilityRules);
        LogMessage log=new LogMessage(1, "Rêgle Comptable mise à jour avec succès");
        logs.add(log);
        return logs;
    }


    
}
