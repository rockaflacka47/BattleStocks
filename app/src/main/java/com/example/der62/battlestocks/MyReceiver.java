package com.example.der62.battlestocks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyReceiver extends BroadcastReceiver {

    ArrayList<HashMap> availableStocks = null;
    ArrayList<Stock> stocks = null;
    DatabaseReference reference;

    @Override
    public void onReceive(Context context, Intent intent) {
        // start battle stocks app
        Intent activityIntent = new Intent(context, Login.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);

        // get the percent decrease from broadcasted intent
        final Double pct = new Double(intent.getStringExtra("pct_decrease"));
        Toast.makeText(context, "CRASH! The market just went down " + pct + "%, good luck!", Toast.LENGTH_LONG).show();

        //do DB stuff
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        // Get list of stocks
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
                Stock s = new Stock();
                ArrayList<Stock> stocks = s.hashMapToStock(availableStocks);
                for(Stock stock: stocks){
                    Double price = stock.getPrice();
                    stock.setPrice(price * (1-(pct/100)));
                    System.out.println(stock.getName());
                    System.out.println(stock.getPrice());
                }
                reference.child("AvailableStocks").setValue(stocks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

}
