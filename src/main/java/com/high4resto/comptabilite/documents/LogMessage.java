package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "logMessage")
public class LogMessage {
    @Id
    private String id;
    @Getter @Setter
    private int level;
    @Getter @Setter
    private String message;
    public LogMessage(int level, String message) {
        this.level = level;
        this.message = message;
    }
}
