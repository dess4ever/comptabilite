package com.high4resto.comptabilite.dataStruct;

import lombok.Getter;
import lombok.Setter;

public class CamenbertPart {
    @Getter @Setter
    private double value;
    @Getter @Setter
    private String color;

    public CamenbertPart(double value,String color) {
        this.value = value;
        this.color = color;
    }
}
