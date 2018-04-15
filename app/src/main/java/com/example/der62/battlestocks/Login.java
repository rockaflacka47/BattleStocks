package com.example.der62.battlestocks;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    protected FirebaseUser user = null;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<HashMap> availableStocks;
    MyReceiver receiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mStatusTextView = findViewById(R.id.createAccount);
        mDetailTextView = findViewById(R.id.submit);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        registerReceiver(receiver, new IntentFilter("edu.pitt.cs1699.team9.CRASH"));

        database = FirebaseDatabase.getInstance();
        //connect to DB
        reference = database.getReference();

        // Get list of stocks
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Get intent
        Intent intent = getIntent();
        String action = intent.getAction();

        // Handle each intent correctly
        if("edu.pitt.cs1699.team9.NEW_STOCK".equals(action)) {
            newStock(intent);
        }else if("edu.pitt.cs1699.team9.PRICE_CHANGE".equals(action)){
            priceChange(intent);
        }else if("edu.pitt.cs1699.team9.OFF_MARKET".equals(action)){
            offMarket(intent);
        }
    }

    public void newStock(Intent intent) {
        Bundle extras = intent.getExtras();
        final String name = extras.getString("company").replaceAll(" ", "-");
        final Double price = Double.valueOf(extras.getString("price"));

        // Get list of stocks
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Stock stock = new Stock();
                String abbr = name;
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();

                if (name.length() >= 4) {
                    abbr = name.substring(0, 4);
                }

                ArrayList<Stock> updatedStocks = stock.hashMapToStock(availableStocks);
                abbr = abbr.toUpperCase();
                updatedStocks.add(new Stock(name, abbr, price));
                reference.child("AvailableStocks").setValue(updatedStocks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
        Toast.makeText(this, "New stock added! " + name + " was added at $" + price + " per share.", Toast.LENGTH_SHORT).show();
    }

    public void priceChange(Intent intent) {
        Bundle extras = intent.getExtras();
        final String name = extras.getString("company");
        double price = 0.0;

        if (extras.getString("price") != null) {
            try {
                price = Double.valueOf(extras.getString("price"));
            } catch (Exception e1) {
                Toast.makeText(Login.this, "Failed to get amount of price change!", Toast.LENGTH_LONG).show();
            }
        }

        final double finPrice = price;

        // Get list of stocks
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Stock stock = new Stock();
                String abbr = name;
                availableStocks = (ArrayList<HashMap>) dataSnapshot.child("AvailableStocks").getValue();

                if (availableStocks == null) {
                    Toast.makeText(Login.this, "Could not find stocks for " + name + ".The list of available stocks is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    Stock stock1 = new Stock();
                    ArrayList<Stock> updatedStocks = stock1.hashMapToStock(availableStocks);

                    boolean updatedStock = false;
                    double oldPrice = 0;
                    double newPrice = 0;
                    for (Stock stk : updatedStocks) {
                        if (stk.name.equals(name)) {
                            oldPrice = stk.price;
                            stk.price += finPrice;
                            newPrice = stk.price;
                            updatedStock = true;
                            break;
                        }
                    }

                    if (!updatedStock) {
                        Toast.makeText(Login.this, "Could not find stocks for " + name, Toast.LENGTH_SHORT).show();
                    } else {
                        reference.child("AvailableStocks").setValue(updatedStocks);
                        Toast.makeText(Login.this, "Stock updated! Price of " + name + " changed from $" + oldPrice +
                                " per share to $" + newPrice + " per share!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    public void offMarket(Intent intent){
        Bundle extras = intent.getExtras();
        //TODO: Pull correct extra names out of the bundle
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
        // re-register receiver because we removed it onStop()
        registerReceiver(receiver, new IntentFilter("edu.pitt.cs1699.team9.CRASH"));
    }

    public void createAccount(String email, String password){
        if (!validateForm()) {
            return;
        }
        if(checkInput(email, password)){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password);
        signInNew(email, password);
    }

    private void signInNew(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            DatabaseReference myRef = database.getReference();
                            DatabaseReference currUser = myRef.child("Users").child(user.getUid());
                            currUser.child("email").setValue(email);
                            currUser.child("money").setValue(2000.0);
                            goHome();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean checkInput(String email, String password){
        if(password.equals("")|| email.equals("")){
            Toast.makeText(Login.this, "Please fill out all sections", Toast.LENGTH_LONG).show();
            return true;
        } else if(password.length() < 6){
            Toast.makeText(Login.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            return true;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Login.this, "Email must be valid", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            goHome();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        //if (!task.isSuccessful()) {
                        //    mStatusTextView.setText("Authorization Failed");
                        //}
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        mAuth.signOut();
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.createAccount) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.submit) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }
}
