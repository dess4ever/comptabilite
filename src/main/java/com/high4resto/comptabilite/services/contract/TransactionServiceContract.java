package com.high4resto.comptabilite.services.contract;

import java.util.List;
import java.util.Map;

import com.high4resto.comptabilite.dataStruct.TreeDictionnary;
import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.documents.Transaction;

public interface TransactionServiceContract {
    public abstract List<Transaction> getAllTransactions();
    public abstract List<LogMessage> saveTransaction(Transaction transaction);
    public abstract List<LogMessage> deleteTransaction(Transaction transaction);
    public abstract List<LogMessage> updateTransaction(Transaction transaction);
    public abstract Map<String,TreeDictionnary> getTreeDictionary();
    public abstract Transaction calculateTotal(Transaction newTransaction);
    public abstract Transaction importTransaction(String content);
}
