package com.example.der62.battlestocks;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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

    public static void syncPriceWithDB(){
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currUser = mAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap> availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
                ArrayList<HashMap> ownedStocks = (ArrayList<HashMap>) dataSnapshot.child("Users").child(currUser.getUid()).child("Stocks").getValue();

                if(availableStocks == null)
                    availableStocks = new ArrayList<>();

                if(ownedStocks == null)
                    ownedStocks = new ArrayList<>();

                for(int i = 0; i < ownedStocks.size(); i++){
                    String currName = (String)ownedStocks.get(i).get("name");

                    boolean found = false;
                    for(int j = 0; j < availableStocks.size(); j++){
                        String currAvailableName = (String)availableStocks.get(j).get("name");
                        if(currName.equals(currAvailableName)){
                            ownedStocks.get(i).remove("price");
                            ownedStocks.get(i).put("price", Double.valueOf("" + availableStocks.get(j).get("price")));
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        ownedStocks.remove(i--);
                    }
                }
                reference.child("Users").child(currUser.getUid()).child("Stocks").setValue(ownedStocks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
