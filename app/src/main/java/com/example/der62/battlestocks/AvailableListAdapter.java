package com.example.der62.battlestocks;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

//Explanation: https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android

public class AvailableListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list;
    private Context context;
    private DatabaseReference user;
    private ArrayList<HashMap> owned;
    private FirebaseUser currUser;
    private double userBalance;

    public AvailableListAdapter(ArrayList<String> list, Context context, DatabaseReference user, ArrayList<HashMap> owned, FirebaseUser currUser) {
        this.list = list;
        this.context = context;
        this.user = user;
        this.owned = owned;
        this.currUser = currUser;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public  Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_available_list, null);
        }

        //Handle TextView and display string from your list
        TextView stockInfo = view.findViewById(R.id.stockInfo);
        stockInfo.setText(list.get(position));

        //Handle buttons and add onClickListeners
        final Button buyButton = view.findViewById(R.id.buyButton);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users/" + currentUser.getUid());
        final DatabaseReference userStocksRef = database.getReference("Users/" + currentUser.getUid() + "/Stocks");

        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String text = list.get(position);
                final String company = text.substring(0, text.indexOf(" "));
                final Query userStocksQuery = userStocksRef.orderByChild("name").equalTo(company);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userBalance = Double.valueOf((String) dataSnapshot.child("money").getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

                userStocksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot1) {
                        if (dataSnapshot1.exists()) {

                            Iterable<DataSnapshot> it = dataSnapshot1.getChildren();
                            double tPrice = 0;

                            for(DataSnapshot ch : it){
                                tPrice = new Double((String) ch.child("price").getValue());
                            }

                            final double price = tPrice;

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    double balance = Double.valueOf((String) dataSnapshot2.child("money").getValue());
                                    if (price > balance) {
                                        Toast.makeText(context, "You do not have enough money", Toast.LENGTH_SHORT);
                                    } else {



                                        Iterable<DataSnapshot> it = dataSnapshot1.getChildren();
                                        long shares = 0;

                                        for(DataSnapshot ch : it){
                                            shares = (long)ch.child("shares").getValue();
                                        }



                                        Iterable<DataSnapshot> it1 = dataSnapshot1.getChildren();
                                        DataSnapshot ref = null;

                                        for(DataSnapshot ch : it1){
                                            ref = ch;
                                        }

                                        userStocksRef.child(ref.getKey()).child("shares").setValue(shares + 1);

                                        userRef.child("money").setValue(Double.valueOf(balance - price).toString());

                                        ((Trade) context).updateLists();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        } else {
                            DatabaseReference stocksRef = database.getReference("AvailableStocks");
                            Query stocksQuery = stocksRef.orderByChild("name").equalTo(company);
                            stocksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Log.d("TAG", ""+ (long)dataSnapshot.child("price").getValue());
                                    Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                                    double tPrice = 0;

                                    for(DataSnapshot ch : it){
                                        tPrice = new Double((String)ch.child("price").getValue());
                                    }

                                    final double price = tPrice;

                                    userStocksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            long count = dataSnapshot.getChildrenCount();
                                            userStocksRef.child(Long.toString(count)).child("name").setValue(company);
                                            userStocksRef.child(Long.toString(count)).child("price").setValue(Double.toString(price));
                                            userStocksRef.child(Long.toString(count)).child("shares").setValue(1);
                                            userRef.child("money").setValue(Double.valueOf(userBalance - price).toString());

                                            ((Trade) context).updateLists();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
