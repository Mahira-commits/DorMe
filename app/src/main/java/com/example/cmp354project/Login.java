package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btn_li_login;
    EditText et_li_email, et_li_password;
    private FirebaseAuth mAuth; //TS member variable
    String TAG = "login class";
    public FirebaseUser u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        btn_li_login = (Button) findViewById(R.id.btn_li_login);
        et_li_password = (EditText) findViewById(R.id.et_li_password);
        et_li_email = (EditText) findViewById(R.id.et_li_email);

        btn_li_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if( et_li_email.getText().toString().isEmpty()){
            et_li_email.setError( "Email is required!" );
            return;
        }
        if (et_li_password.getText().toString().isEmpty()){
            et_li_password.setError( "Password is required!" );
            return;
        }
            //Create a new signIn method which takes in an email address and password,
            // validates them, and then signs a user in with the signInWithEmailAndPassword method.
            mAuth.signInWithEmailAndPassword(et_li_email.getText().toString(), et_li_password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "Authentication Success.", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(), HomePage.class);  //create class
                                i.putExtra("currentUserEmail", user.getEmail());
                                startActivity(i);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }


                    });
    }


}