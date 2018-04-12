package com.example.der62.battlestocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

//Explanation: https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HoldingsListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list;
    private Context context;

    public HoldingsListAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
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
            view = inflater.inflate(R.layout.layout_holdings_list, null);
        }

        //Handle TextView and display string from your list
        TextView stockInfo = view.findViewById(R.id.stockInfo);
        stockInfo.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button sellButton = view.findViewById(R.id.sellButton);
        Button buyButton = view.findViewById(R.id.buyButton);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users/" + currentUser.getUid());
        final DatabaseReference userStocksRef = database.getReference("Users/" + currentUser.getUid() + "/Stocks");

        sellButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String text = list.get(position);
                String company = text.substring(0, text.indexOf(" "));
                final Query stocksQuery = userStocksRef.orderByChild("name").equalTo(company);
                stocksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot1) {
                        long shares = (long) dataSnapshot1.getChildren().iterator().next().child("shares").getValue();
                        if (shares > 0) {
                            if (shares == 1) {
                                userStocksRef.child(dataSnapshot1.getChildren().iterator().next().getKey()).removeValue();
                                ((Trade) context).updateLists();
                            } else {
                                userStocksRef.child(dataSnapshot1.getChildren().iterator().next().getKey()).child("shares").setValue(shares - 1);
                                final double price = Double.valueOf((String) dataSnapshot1.getChildren().iterator().next().child("price").getValue());
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        double balance = Double.valueOf((String) dataSnapshot2.child("money").getValue());
                                        userRef.child("money").setValue(Double.valueOf(balance + price).toString());

                                        ((Trade) context).updateLists();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                        } else {
                            Toast.makeText(context, "You cannot have negative shares", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                notifyDataSetChanged();
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String text = list.get(position);
                String company = text.substring(0, text.indexOf(" "));
                final Query stocksQuery = userStocksRef.orderByChild("name").equalTo(company);
                stocksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot1) {
                        final double price = Double.valueOf((String) dataSnapshot1.getChildren().iterator().next().child("price").getValue());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                double balance = Double.valueOf((String) dataSnapshot2.child("money").getValue());
                                if (price > balance) {
                                    Toast.makeText(context, "You do not have enough money", Toast.LENGTH_SHORT);
                                } else {
                                    long shares = (long) dataSnapshot1.getChildren().iterator().next().child("shares").getValue();

                                    Iterable<DataSnapshot> it = dataSnapshot1.getChildren();
                                    DataSnapshot ref = null;

                                    for(DataSnapshot ch : it){
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
