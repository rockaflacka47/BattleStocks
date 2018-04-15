package com.example.der62.battlestocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currUser;

    ArrayList<HashMap> ownedStocksHash;
    ArrayList<OwnedStock> ownedStocksObj;

    double balance = 0.0;
    double netWorth = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        OwnedStock.syncPriceWithDB();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        //When the firebase changes update our data and then our gui

        reference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                balance = dataSnapshot.child("Users").child(currUser.getUid()).child("money").getValue(Double.class);
                ownedStocksHash = (ArrayList<HashMap>) dataSnapshot.child("Users").child(currUser.getUid()).child("Stocks").getValue();
                ownedStocksObj = new ArrayList<>();
                netWorth = 0;

                if(ownedStocksHash != null) {
                    for (HashMap h : ownedStocksHash) {
                        OwnedStock os = new OwnedStock((String) h.get("name"), (String) h.get("abbv"), Double.parseDouble(("" + h.get("price"))== null || ("" + h.get("price")).equals("")?"1":""+h.get("price")), Integer.parseInt((("" + h.get("shares"))==null || ("" + h.get("shares")).equals("")?"1":""+h.get("shares"))));

                        ownedStocksObj.add(os);
                    }

                    for (OwnedStock os : ownedStocksObj) {
                        netWorth += os.getShares() * os.getPrice();
                    }
                }
                updateViews();
            }

            public void onCancelled(DatabaseError err) {
                //do nothing
            }
        });
    }

    //Opens the holdings activity
    public void goHoldings(View v){
        Intent intent = new Intent(this, Holdings.class);
        startActivity(intent);
    }

    //Whenever we pull from firebase this updates the data in the activity
    public void updateViews(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        TextView netWorthTV = findViewById(R.id.NetWorthTV);
        TextView balanceTV = findViewById(R.id.BalanceTV);

        DatabaseReference myRef = database.getReference();
        DatabaseReference userRef = myRef.child("Users").child(currUser.getUid());

        netWorthTV.setText("Net Worth: $" + df.format(netWorth + balance ));
        balanceTV.setText("Balance: $" + df.format(balance));
    }
}
