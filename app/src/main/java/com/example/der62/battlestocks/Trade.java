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

import java.util.ArrayList;
import java.util.HashMap;

public class Trade extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currUser;
    ArrayList<OwnedStock> ownedStocks;
    ArrayList<HashMap> availableStocks;
    ListView list;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownedStocks = (ArrayList<OwnedStock>) dataSnapshot.child(currUser.getUid()).child("OwnedStocks").getValue();
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
                ArrayList<String> forList = new ArrayList<>();
                for(int i = 0; i < availableStocks.size(); i++){
                    forList.add(availableStocks.get(i).get("name") + " : " + availableStocks.get(i).get("price"));
                }
                list = (ListView) findViewById(R.id.available);
                arrayAdapter = new ArrayAdapter<String>(
                        Trade.this,
                        android.R.layout.simple_list_item_1,
                         forList);

                list.setAdapter(arrayAdapter);
                //reference.child("AvailableStocks").setValue(availableStocks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
