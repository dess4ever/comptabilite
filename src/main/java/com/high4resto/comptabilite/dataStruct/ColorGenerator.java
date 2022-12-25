package com.high4resto.comptabilite.dataStruct;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import lombok.Getter;
import lombok.Setter;

public class ColorGenerator {
    @Getter @Setter
    private List<Color> palette;

    public ColorGenerator(int n)
    {
        this.palette = new ArrayList<Color>();
        float step=1.0f/n;
        for (int i = 0; i < n; i++) {
            float hue = step * i;
            float saturation = 0.9f;
            float brightness = 0.9f;
            palette.add(Color.getHSBColor(hue, saturation, brightness));
        }       
    }

    // convert a color to web format
    private String convertColorToWeb(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    // return a random color from the palette
    public Color pullRandomColor()
    {
        int index = (int)(Math.random() * palette.size());
        return palette.remove(index);
    }

    // return a random color from the palette and convert it to web format
    public String PullRandomColorAsWeb()
    {
        return convertColorToWeb(pullRandomColor());
    }
    
}
