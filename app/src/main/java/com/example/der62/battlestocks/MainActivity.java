package com.example.der62.battlestocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<HashMap> availableStocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goTrade(View v){
        Intent intent = new Intent(this, Trade.class);
        startActivity(intent);
    }
    public void goProfile(View v){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void goPhoto(View view){
        Intent intent = new Intent("com.group10.photoeditor");
        intent.setPackage("com.group10.photoeditor");
        intent.putExtra("Activity", "RandomActivity");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

}