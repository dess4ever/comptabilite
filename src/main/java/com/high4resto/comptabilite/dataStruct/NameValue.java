package com.high4resto.comptabilite.dataStruct;

import lombok.Getter;
import lombok.Setter;

public class NameValue {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private double value;
    public NameValue(String name, double value) {
        this.name = name;
        this.value = value;
    }
}

