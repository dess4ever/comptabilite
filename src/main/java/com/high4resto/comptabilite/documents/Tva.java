package com.high4resto.comptabilite.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tva")
public class Tva {
    @Id
    private String id;
    private String code;
    private double value;
    private Date validity;

    public Date getValidity() {
        return validity;
    }
    public void setValidity(Date validity) {
        this.validity = validity;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
