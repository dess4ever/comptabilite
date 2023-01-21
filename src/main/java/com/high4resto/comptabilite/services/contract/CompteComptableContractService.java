package com.high4resto.comptabilite.services.contract;

import java.util.List;

import com.high4resto.comptabilite.documents.CompteComptale;
import com.high4resto.comptabilite.documents.LogMessage;

public interface CompteComptableContractService {
    public abstract List<CompteComptale> getAllCompteComptable();
    public abstract List<LogMessage> saveCompteComptable(CompteComptale compteComptable);
    public abstract List<LogMessage> deleteCompteComptable(CompteComptale compteComptable);
    public abstract List<LogMessage> updateCompteComptable(CompteComptale compteComptable);
    public abstract CompteComptale searchCompteComptableWhithCode(String code);
}
