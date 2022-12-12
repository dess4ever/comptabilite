package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.Tva;

public interface TvaRepository extends MongoRepository<Tva,String>{
    
}
