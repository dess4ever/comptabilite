package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.Item;

public interface ItemRepository extends MongoRepository<Item, String> {
    public Item findByName(String name);
}
