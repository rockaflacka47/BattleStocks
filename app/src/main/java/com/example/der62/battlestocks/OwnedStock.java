package com.example.der62.battlestocks;

//class for stocks you own rather then just exist
public class OwnedStock extends Stock{
    private int ownedShares;

    public OwnedStock(){
        ownedShares = 0;
    }
    public OwnedStock(int purchased){
        ownedShares = purchased;
    }
    public OwnedStock(String name, String abbv, double price, int purchased){
        super(name, abbv, price);
        ownedShares = purchased;
    }

    public int getShares(){
        return ownedShares;
    }

    //sets owned shares to current owned plus purchased
    public void setOwnedShared(int purchased){
        ownedShares += purchased;
    }
}
