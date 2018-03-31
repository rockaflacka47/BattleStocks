package com.example.der62.battlestocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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

}