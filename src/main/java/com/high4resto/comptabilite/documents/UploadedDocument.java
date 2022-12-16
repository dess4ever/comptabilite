package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;


@Document
public class UploadedDocument {
    @Id
    private String id;
    @Getter @Setter
    private String fileName;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String description;
    @Getter @Setter @Indexed(unique = true)
    private String hash;
    @Getter @Setter
    private byte[] content;
    @Getter @Setter
    private String date;


}
