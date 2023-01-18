package com.high4resto.comptabilite.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.documents.TransactionFilter;
import com.high4resto.comptabilite.repository.TransactionsFilterRepository;
import com.high4resto.comptabilite.services.contract.TransactionFilterServiceContract;

@Service
public class TransactionFilterService implements TransactionFilterServiceContract {
    
    @Autowired
    private TransactionsFilterRepository transactionsFilterRepository;

    @Override
    public List<TransactionFilter> getAllTransactionFilters() {
        return transactionsFilterRepository.findAll();
    }

    @Override
    public List<LogMessage> saveTransactionFilter(TransactionFilter transactionFilter) {
        List<LogMessage> logMessages = new ArrayList<>();
        if(transactionFilter!=null && transactionFilter.getName()!=null && !transactionFilter.getName().isEmpty())
        {
            transactionsFilterRepository.save(transactionFilter);
            logMessages.add(new LogMessage(1,"Filtre de transactions enregistré" ));
            return logMessages;
        }
        return logMessages;
    }

    @Override
    public List<LogMessage> deleteTransactionFilter(TransactionFilter transactionFilter) {
        List<LogMessage> logMessages = new ArrayList<>();
        if(transactionFilter!=null && transactionFilter.getName()!=null && !transactionFilter.getName().isEmpty())
        {
            transactionsFilterRepository.delete(transactionFilter);
            logMessages.add(new LogMessage(1,"Filtre de transactions supprimé" ));
            return logMessages;
        }
        return logMessages;
    }

    @Override
    public List<LogMessage> updateTransactionFilter(TransactionFilter transactionFilter) {
        List<LogMessage> logMessages = new ArrayList<>();
        if(transactionFilter!=null && transactionFilter.getName()!=null && !transactionFilter.getName().isEmpty())
        {
            transactionsFilterRepository.save(transactionFilter);
            logMessages.add(new LogMessage(1,"Filtre de transactions mis à jour" ));
            return logMessages;
        }
        return logMessages;
    }
    
}
