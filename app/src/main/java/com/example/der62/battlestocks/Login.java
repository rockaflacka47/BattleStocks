package com.example.der62.battlestocks;

import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
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
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    protected FirebaseUser user = null;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mStatusTextView = findViewById(R.id.createAccount);
        mDetailTextView = findViewById(R.id.submit);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();

    }

    public void createAccount(final String email, String password){
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
                            currUser.child("money").setValue(2000);
                            goHome();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
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
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText("Authorization Failed");
                        }
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
