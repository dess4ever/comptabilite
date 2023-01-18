package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.ItemFilter;

public interface ItemFilterRepository extends MongoRepository<ItemFilter, String>{
    
}
