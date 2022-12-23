package com.high4resto.comptabilite.documents;

import lombok.Getter;
import lombok.Setter;

public class StockItem {
    @Getter @Setter
    private int quantity;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private Item item;
    @Getter @Setter
    private double price;
    public StockItem(int quantity, String name, Item item,Double price) {
        this.quantity = quantity;
        this.name = name;
        this.item = item;
        this.price=price;
    }
}
