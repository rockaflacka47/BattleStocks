package com.example.der62.battlestocks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OffMarketService extends Service {
    public OffMarketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("A stock has left the market!")
                .build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        Intent activityIntent = new Intent(getApplicationContext(), Login.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(activityIntent);

        final String company = intent.getExtras().getString("company");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebase.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Stock stock = new Stock();
                ArrayList<HashMap> availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();

                boolean found = false;
                for(int i = 0; i < availableStocks.size(); i++){
                    if(availableStocks.get(i).get("name").equals(company)){
                        availableStocks.remove(i);
                        found = true;
                        break;
                    }
                }

                if(!found){
                    Toast.makeText(OffMarketService.this, "Could not find " + company + " on market!", Toast.LENGTH_LONG).show();
                }else {
                    reference.child("AvailableStocks").setValue(availableStocks);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });

        stopSelf(startID);
        return START_STICKY;
    }
}
