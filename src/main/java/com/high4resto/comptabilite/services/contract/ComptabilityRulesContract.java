package com.high4resto.comptabilite.services.contract;

import java.util.List;

import com.high4resto.comptabilite.documents.ComptabilityRules;
import com.high4resto.comptabilite.documents.LogMessage;

public interface ComptabilityRulesContract {
    public abstract List<ComptabilityRules> getAllComptabilityRules();
    public abstract List<LogMessage> saveComptabilityRules(ComptabilityRules comptabilityRules);
    public abstract List<LogMessage> deleteComptabilityRules(ComptabilityRules comptabilityRules);
    public abstract List<LogMessage> updateComptabilityRules(ComptabilityRules comptabilityRules);    
}
