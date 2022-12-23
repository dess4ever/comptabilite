package com.high4resto.comptabilite.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.high4resto.comptabilite.documents.VendorInvoice;

public interface VendorInvoiceRepository extends MongoRepository<VendorInvoice, String> {
    
}
