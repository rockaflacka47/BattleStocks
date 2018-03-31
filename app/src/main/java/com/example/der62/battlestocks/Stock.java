package com.example.der62.battlestocks;

/**
 * Created by DER62 on 3/31/2018.
 */
//represents a single stock
public class Stock {
    protected String name;
    protected String abbv;
    protected double price;
    public Stock(){
        name = null;
        abbv = null;
        price = -1;
    }

    public Stock(String name, String abbv, double price){
        this.name = name;
        this.abbv = abbv;
        this.price = price;
    }

    public String getName(){
        return name;
    }
    public String getAbbv(){
        return abbv;
    }
    public double getPrice(){
        return price;
    }
}
