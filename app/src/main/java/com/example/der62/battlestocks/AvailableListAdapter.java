package com.example.der62.battlestocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

//Explanation: https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android

public class AvailableListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list;
    private Context context;
    private DatabaseReference user;
    private ArrayList<HashMap> owned;
    private FirebaseUser currUser;

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

        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //buy stock
                int numOwned = 0;
                if(owned.contains(list.get(position))){

                }
                //owned.add(list.get(position).split(":")[1], )
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
