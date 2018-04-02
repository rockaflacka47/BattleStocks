package com.example.der62.battlestocks;

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
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        holdingsListString = new ArrayList<>();

        lv = findViewById(R.id.holdingsList);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, holdingsListString));

        //on data change update our list of holdings
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownedStocks = (ArrayList<HashMap>) dataSnapshot.child("Users").child(currUser.getUid()).child("Stocks").getValue();
                ownedStocksObj = new ArrayList<>();
                holdingsListString = new ArrayList<>();

                if(ownedStocks != null) {
                    for (HashMap h : ownedStocks) {
                        OwnedStock os = new OwnedStock((String) h.get("name"), (String) h.get("abbv"), (double) h.get("price"), (int) h.get("shares"));
                        ownedStocksObj.add(os);
                    }

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(2);

                    for (OwnedStock os : ownedStocksObj) {
                        holdingsListString.add(os.getAbbv() + " : " + os.getShares() + " shares - $" + df.format(os.getShares() * os.getPrice()));
                    }
                }
            }

            public void onCancelled(DatabaseError err) {
                //do nothing
            }
        });

    }
}
