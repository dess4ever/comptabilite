package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.CompteComptale;

public interface CompteComptableRepository extends MongoRepository<CompteComptale, String> {
    public CompteComptale findByCode(String code);
}
