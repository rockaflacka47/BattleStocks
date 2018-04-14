package com.example.der62.battlestocks;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void setPrice(double newPrice){
        price = newPrice;
    }

    //Takes an ArrayList of HashMap and parses it into an ArrayList of Stock
    public ArrayList<Stock> hashMapToStock(ArrayList<HashMap> stocksList){
        if(stocksList == null){
            stocksList = new ArrayList<>();
        }

        ArrayList<Stock> ret = new ArrayList<>();
        for(int i = 0; i < stocksList.size(); i++){
            if(stocksList.get(i).get("price").getClass() == Long.class){
                stocksList.get(i).put("price", new Double((Long)stocksList.get(i).get("price")));
            }
            ret.add(new Stock((String)stocksList.get(i).get("name"), (String)stocksList.get(i).get("abbv"), (double)stocksList.get(i).get("price")));
        }
        return ret;
    }
}
