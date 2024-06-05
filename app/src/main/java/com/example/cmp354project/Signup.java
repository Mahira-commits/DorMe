
package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    EditText et_si_fname, et_si_lname, et_si_email, et_si_password, et_si_dormNum;
    Spinner spinner_si_dormBlock;
    Button btn_si_signup;
    private FirebaseAuth mAuth; // TS member variable
    String TAG = "sign up class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_si_fname = (EditText) findViewById(R.id.et_si_fname);
        et_si_lname = (EditText) findViewById(R.id.et_si_lname);
        et_si_email = (EditText) findViewById(R.id.et_si_email);
        et_si_password = (EditText) findViewById(R.id.et_si_password);
        spinner_si_dormBlock = (Spinner) findViewById(R.id.spinner_si_dormBlock);
        btn_si_signup = (Button) findViewById(R.id.btn_si_signup);
        et_si_dormNum = (EditText) findViewById(R.id.et_si_dormNum);

        btn_si_signup.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        String[] blocks = { "A", "B", "C", "D", "E", "F" };
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, blocks);
        spinner_si_dormBlock.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        if (et_si_fname.getText().toString().isEmpty()) {
            et_si_fname.setError("First name is required!");
            return;
        }

        // add a restriction on length

        if (et_si_lname.getText().toString().isEmpty()) {
            et_si_lname.setError("Last name is required!");
            return;
        }

        // add a restriction on length

        if (et_si_email.getText().toString().isEmpty()) {
            et_si_email.setError("Email is required!");
            return;
        }

        // restriction on aus email

        if (!et_si_email.getText().toString().split("@")[1].equals("aus.edu")){
            et_si_email.setError("An AUS Email is required!");
            return;
        }

        if (et_si_password.getText().toString().isEmpty()) {
            et_si_password.setError("Password is required!");
            return;
        }

        if (et_si_password.getText().toString().length() < 6) {
            et_si_password.setError("Password must be at least 6 characters!");
            return;
        }



        if (spinner_si_dormBlock.getSelectedItem() == null) {
            ((TextView) spinner_si_dormBlock.getSelectedView()).setError("Dorm Block is required!");
        }

        if (et_si_dormNum.getText().toString().isEmpty()) {
            et_si_dormNum.setError("Dorm Number is required!");
            return;
        }

        if (Integer.parseInt(et_si_dormNum.getText().toString()) < 100 || Integer.parseInt(et_si_dormNum.getText().toString()) > 240) {
            et_si_dormNum.setError("Invalid Dorm Number! (100-140 and 200-240)");
            return;
        }
        if (Integer.parseInt(et_si_dormNum.getText().toString()) > 140 && Integer.parseInt(et_si_dormNum.getText().toString()) < 200) {
            et_si_dormNum.setError("Invalid Dorm Number! (100-140 and 200-240)");
            return;
        }

        mAuth.createUserWithEmailAndPassword(et_si_email.getText().toString(), et_si_password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Create User With Email : success!",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            CollectionReference UserProfiles = MainActivity.db.collection("UserProfiles");

                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("fname", et_si_fname.getText().toString());
                            userProfile.put("lname", et_si_lname.getText().toString());
                            userProfile.put("email", et_si_email.getText().toString());
                            userProfile.put("password", et_si_password.getText().toString());
                            userProfile.put("dormBlock", spinner_si_dormBlock.getSelectedItem());
                            userProfile.put("dormNum", et_si_dormNum.getText().toString());
                            userProfile.put("points", "10");

                            UserProfiles.document(et_si_email.getText().toString()).set(userProfile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Successfully Added! Please Login!",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), Login.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Error adding document",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}