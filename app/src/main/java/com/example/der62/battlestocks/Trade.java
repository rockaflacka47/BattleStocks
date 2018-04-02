package com.example.der62.battlestocks;

import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Trade extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currUser;
    ArrayList<HashMap> ownedStocks;
    ArrayList<HashMap> availableStocks;
    ListView list;
    ListView list2;
    AvailableListAdapter arrayAdapter;
    HoldingsListAdapter currAdapter;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownedStocks = (ArrayList<HashMap>) dataSnapshot.child("Users").child(currUser.getUid()).child("Stocks").getValue();
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
                /*if(ownedStocks == null){
                    ownedStocks = new ArrayList<>();
                    ownedStocks.add(new OwnedStock("Apple", "APPL", 512, 2));
                    ownedStocks.add(new OwnedStock("Amazon", "AMZN", 800, 1));
                    ownedStocks.add(new OwnedStock("Microsoft", "MSFT", 400, 3));
                    ownedStocks.add(new OwnedStock("Google", "GOOGL", 1012, 4));
                }*/
                reference.child("Users").child(currUser.getUid()).child("Stocks").setValue(ownedStocks);
                ArrayList<String> forList = new ArrayList<>();
                for (int i = 0; i < availableStocks.size(); i++) {
                    forList.add(availableStocks.get(i).get("name") + " : $" + availableStocks.get(i).get("price"));
                }
                list = findViewById(R.id.availableStocksList);
                arrayAdapter = new AvailableListAdapter(forList, Trade.this, reference, ownedStocks, currUser);
                list.setAdapter(arrayAdapter);
                ArrayList<String> currOwnedList = new ArrayList<>();
                for (int i = 0; i < ownedStocks.size(); i++) {
                    currOwnedList.add(ownedStocks.get(i).get("name") + " : " + ownedStocks.get(i).get("shares") + " shares owned");
                }
                currAdapter = new HoldingsListAdapter(currOwnedList, Trade.this);
                list2 = (ListView) findViewById(R.id.currentHoldingsList);
                list2.setAdapter(currAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(listener);
    }
}
