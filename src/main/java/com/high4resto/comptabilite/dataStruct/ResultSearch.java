package com.high4resto.comptabilite.dataStruct;

import com.high4resto.comptabilite.documents.UploadedDocument;
import com.high4resto.comptabilite.utils.TextUtil;

import lombok.Getter;
import lombok.Setter;

public class ResultSearch implements Comparable<ResultSearch> {
    @Getter @Setter
    private double score;
    @Getter @Setter
    private UploadedDocument document;
    @Override
    public int compareTo(ResultSearch o) {
        if(this.score>o.score)
            return -1;
        else if(this.score<o.score)
            return 1;
        else
            return 0;
    }
    public  ResultSearch(String sentence, UploadedDocument document) {
        this.score = TextUtil.search(sentence, document.getBrut());
        this.document = document;
    }
    public ResultSearch(UploadedDocument document) {
        this.document = document;
        this.score = 0;
    }
}
