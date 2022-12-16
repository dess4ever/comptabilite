package com.high4resto.comptabilite.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.Rib;

public interface RibRepository extends MongoRepository<Rib, String>{
    
}
