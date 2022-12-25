package com.high4resto.comptabilite.dataStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import lombok.Getter;
import lombok.Setter;

public class Camenbert {
    @Getter @Setter
    private Map<String,CamenbertPart> parts=new HashMap<String,CamenbertPart>();
    
    private void create(Map<String,CamenbertPart> parts) {
        this.parts=parts;
        int numberOfParts=parts.size();
        ColorGenerator colorGenerator=new ColorGenerator(numberOfParts);

        for (Map.Entry<String, CamenbertPart> entry : parts.entrySet()) {
            entry.getValue().setColor(colorGenerator.PullRandomColorAsWeb());
        }
    }

    public Camenbert(List<NameValue> nameValues) {
        Map<String,CamenbertPart> parts=new HashMap<String,CamenbertPart>();
        for (NameValue nameValue : nameValues) {
            if(parts.containsKey(nameValue.getName()))
            {
                parts.get(nameValue.getName()).setValue(parts.get(nameValue.getName()).getValue()+nameValue.getValue());
            }
            else
                parts.put(nameValue.getName(), new CamenbertPart(nameValue.getValue(),""));
        }
        create(parts);
    }

    // convert data to primefaces piechart model
    public PieChartModel getChartModel() {
        PieChartModel model = new PieChartModel();
        ChartData data = new ChartData();
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        for (Map.Entry<String, CamenbertPart> entry : parts.entrySet()) {;
            values.add(entry.getValue().getValue());
        }
        dataSet.setData(values);
        List<String> bgColors = new ArrayList<>();
        for (Map.Entry<String, CamenbertPart> entry : parts.entrySet()) {;
            bgColors.add(entry.getValue().getColor());
        }
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        for (Map.Entry<String, CamenbertPart> entry : parts.entrySet()) {;
            labels.add(entry.getKey());
        }
        data.setLabels(labels);
        model.setData(data);
        return model;
    }
    
}
