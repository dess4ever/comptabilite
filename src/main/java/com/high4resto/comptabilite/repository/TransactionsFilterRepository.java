package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.TransactionFilter;

public interface TransactionsFilterRepository extends MongoRepository<TransactionFilter, String> {
    
}
