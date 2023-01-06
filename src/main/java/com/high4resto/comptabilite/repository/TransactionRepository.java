package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
}
