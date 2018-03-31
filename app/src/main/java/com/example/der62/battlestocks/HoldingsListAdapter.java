package com.example.der62.battlestocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

//Explanation: https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android

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

        sellButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //sell stock
                notifyDataSetChanged();
            }
        });
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //buy stock
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
