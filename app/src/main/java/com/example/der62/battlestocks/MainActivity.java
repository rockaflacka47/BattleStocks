package com.example.der62.battlestocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<HashMap> availableStocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect to DB
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        // Get list of stocks
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Get intent
        Intent intent = getIntent();
        String action = intent.getAction();

        // Handle each intent correctly
        if("edu.pitt.cs1699.team9.NEW_STOCK".equals(action)) {
            newStock(intent);
        }else if("edu.pitt.cs1699.team9.PRICE_CHANGE".equals(action)){
            priceChange(intent);
        }else if("edu.pitt.cs1699.team9.OFF_MARKET".equals(action)){
            offMarket(intent);
        }else if("edu.pitt.cs1699.team9.CRASH".equals(action)){
            shitsfucked(intent);
        }
    }

    public void newStock(Intent intent){
        Bundle extras = intent.getExtras();
        String name = extras.getString("company");
        Double price = extras.getDouble("price");
        if(availableStocks == null){
            Toast.makeText(this, "There was a problem adding " + name + " please try again.", Toast.LENGTH_SHORT);
        }
        Stock stock = new Stock();
        String abbr = name;
        if(name.length() >= 4){
            abbr = name.substring(0, 4);
        }
        ArrayList<Stock> updatedStocks = stock.hashmapToStock(availableStocks);
        abbr = abbr.toUpperCase();
        updatedStocks.add(new Stock(name, abbr, price));
        reference.child("AvailableStocks").setValue(updatedStocks);
        Toast.makeText(this, "New stock added! " + name + " was added at $" + price + " per share.", Toast.LENGTH_SHORT);
    }

    public void priceChange(Intent intent){
        Bundle extras = intent.getExtras();
        //TODO: Pull correct extra names out of the bundle
    }

    public void offMarket(Intent intent){
        Bundle extras = intent.getExtras();
        //TODO: Pull correct extra names out of the bundle
    }

    public void shitsfucked(Intent intent){
        Bundle extras = intent.getExtras();
        //TODO: Pull correct extra names out of the bundle
    }

    public void goTrade(View v){
        Intent intent = new Intent(this, Trade.class);
        startActivity(intent);
    }
    public void goProfile(View v){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void goPhoto(View view){
        Intent intent = new Intent("com.group10.photoeditor");
        intent.setPackage("com.group10.photoeditor");
        intent.putExtra("Activity", "RandomActivity");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

}