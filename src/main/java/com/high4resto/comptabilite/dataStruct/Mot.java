package com.high4resto.comptabilite.dataStruct;

import lombok.Getter;
import lombok.Setter;

public class Mot implements Comparable<Mot> {
    @Getter @Setter
    private String mot;
    @Getter @Setter
    private int frequence;
    @Override
    public int compareTo(Mot o) {
        if(this.frequence>o.frequence)
            return -1;
        else if(this.frequence<o.frequence)
            return 1;
        else
            return 0;
    }
}
