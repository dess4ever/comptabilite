package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.City;

public interface CityRepository extends MongoRepository<City,String> {
    
}
