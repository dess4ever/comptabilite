package com.high4resto.comptabilite.services.contract;

import java.util.List;

import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.documents.TransactionFilter;

public interface TransactionFilterServiceContract {
    public abstract List<TransactionFilter> getAllTransactionFilters();
    public abstract List<LogMessage> saveTransactionFilter(TransactionFilter transactionFilter);
    public abstract List<LogMessage> deleteTransactionFilter(TransactionFilter transactionFilter);
    public abstract List<LogMessage> updateTransactionFilter(TransactionFilter transactionFilter);
}
