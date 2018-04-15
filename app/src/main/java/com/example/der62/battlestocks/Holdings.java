package com.example.der62.battlestocks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Holdings extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currUser;
    ArrayList<HashMap> ownedStocks;
    ArrayList<OwnedStock> ownedStocksObj;
    ArrayList<String> holdingsListString;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holdings);

        OwnedStock.syncPriceWithDB();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        holdingsListString = new ArrayList<>();
        final ArrayAdapter arrayAdapter;

        lv = findViewById(R.id.holdingsList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, holdingsListString);
        lv.setAdapter(arrayAdapter);

        //on data change update our list of holdings
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownedStocks = (ArrayList<HashMap>) dataSnapshot.child("Users").child(currUser.getUid()).child("Stocks").getValue();
                ownedStocksObj = new ArrayList<>();
                holdingsListString.clear();

                if(ownedStocks != null) {
                    for (HashMap h : ownedStocks) {
                        Log.d("WOOOA", h.toString());
                        OwnedStock os = new OwnedStock((String) h.get("name"), (String) h.get("name"), Double.parseDouble("" + h.get("price")), Integer.parseInt("" + h.get("shares")));
                        ownedStocksObj.add(os);
                    }

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(2);

                    for (OwnedStock os : ownedStocksObj) {
                        holdingsListString.add(os.getAbbv() + " : " + os.getShares() + " shares - $" + df.format(os.getShares() * os.getPrice()));
                        arrayAdapter.notifyDataSetChanged();
                    }

                }else{
                    holdingsListString.add("You currently have no holdings!");
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            public void onCancelled(DatabaseError err) {
                //do nothing
            }
        });

    }
}
