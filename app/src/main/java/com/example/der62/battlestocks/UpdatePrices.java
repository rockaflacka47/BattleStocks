package com.example.der62.battlestocks;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by DER62 on 4/14/2018.
 */

public class UpdatePrices extends IntentService {
    ArrayList<HashMap> availableStocks = null;
    DatabaseReference reference;
    public UpdatePrices(){
        super("UpdatePrices");
    }
    @Override
    protected void onHandleIntent(Intent workIntent){
        //String dataString = workIntent.getDataString();
        /*Thread t = new Thread(new Runnable(){
            @Override
            public void run(){
              while()
            }
        });*/
        while(true){
            try {
               // Thread.sleep(300000);
                Thread.sleep(30000);
            }
            catch(Exception e){

            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            reference = database.getReference();

            // Get list of stocks
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Random rand = new Random();
                    int incOrDec = rand.nextInt(2);
                    double percentChange = rand.nextInt(5);
                    percentChange += 1;
                    availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
                    Stock s = new Stock();
                    ArrayList<Stock> stocks = s.hashMapToStock(availableStocks);
                    for(Stock stock: stocks){
                        Double price = stock.getPrice();
                        if(incOrDec == 0){
                            stock.setPrice(price + ((price * percentChange/100)));
                        }
                        else{
                            stock.setPrice(price - (price * (percentChange/100)));
                        }
                    }
                    reference.child("AvailableStocks").setValue(stocks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
    }
}
