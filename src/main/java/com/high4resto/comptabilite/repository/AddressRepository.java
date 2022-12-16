package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.Address;

public interface AddressRepository extends MongoRepository<Address,String>{
    
}
