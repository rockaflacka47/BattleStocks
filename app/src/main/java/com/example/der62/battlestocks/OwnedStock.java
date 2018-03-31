package com.example.der62.battlestocks;

/**
 * Created by DER62 on 3/31/2018.
 */
//class for stocks you own rather then just exist
public class OwnedStock extends Stock{
    private int ownedShared;

    public OwnedStock(){
        ownedShared = 0;
    }
    public OwnedStock(int purchased){
        ownedShared = purchased;
    }
    public OwnedStock(String name, String abbv, double price, int purchased){
        super(name, abbv, price);
        ownedShared = purchased;
    }

    public int getShares(){
        return ownedShared;
    }

    //sets owned shares to current owned plus purchased
    public void setOwnedShared(int purchased){
        ownedShared += purchased;
    }
}
