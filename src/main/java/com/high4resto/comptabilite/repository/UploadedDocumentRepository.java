package com.high4resto.comptabilite.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.UploadedDocument;

public interface UploadedDocumentRepository extends MongoRepository<UploadedDocument,String> {
    public List<UploadedDocument> findByHash(String hash);
}
