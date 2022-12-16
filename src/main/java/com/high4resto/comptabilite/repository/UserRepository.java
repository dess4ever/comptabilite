package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.User;

public interface UserRepository extends MongoRepository<User, String>{
    public User findByUsername(String username);
}
