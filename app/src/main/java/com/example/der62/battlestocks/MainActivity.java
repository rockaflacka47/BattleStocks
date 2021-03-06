package com.example.der62.battlestocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, UpdatePrices.class);
        startService(intent);
        OwnedStock.syncPriceWithDB();
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



    public void lookUp(View view){
        Intent intent = new Intent("com.group10.photoeditor");
        intent.setPackage("com.group10.photoeditor");
        intent.putExtra("Activity", "LookupActivity");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    public void rename(View view){
        Intent intent = new Intent("com.group10.photoeditor");
        intent.setPackage("com.group10.photoeditor");
        intent.putExtra("Activity", "RenameActivity");
        intent.putExtra("Filename", "image");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }
}