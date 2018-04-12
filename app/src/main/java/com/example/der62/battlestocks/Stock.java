package com.example.der62.battlestocks;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void setPrice(double newPrice){ price = newPrice ; }

    public ArrayList<Stock> hashmapToStock(ArrayList<HashMap> stocks){
        ArrayList<Stock> ret = new ArrayList<>();
        for(int i = 0; i < stocks.size(); i++){
            if(stocks.get(i).get("price").getClass() == Long.class){
                stocks.get(i).put("price", new Double((Long)stocks.get(i).get("price")));
            }
            ret.add(new Stock((String)stocks.get(i).get("name"), (String)stocks.get(i).get("abbv"), Double.valueOf((String)stocks.get(i).get("price"))));
        }
        return ret;
    }
}
