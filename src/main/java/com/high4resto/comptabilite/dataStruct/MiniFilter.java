package com.high4resto.comptabilite.dataStruct;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class MiniFilter implements Serializable {
    @Getter @Setter
    private String key;
    @Getter @Setter
    private String value;
    @Getter @Setter
    private String operator;
}
