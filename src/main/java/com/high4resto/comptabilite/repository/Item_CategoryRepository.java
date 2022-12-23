package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.Item_Category;

public interface Item_CategoryRepository extends MongoRepository<Item_Category, String>{
    public Item_Category findByName(String name);    
}
