package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.ComptabilityRules;

public interface ComptabilityRulesRepository extends MongoRepository<ComptabilityRules, String> {
    
}
