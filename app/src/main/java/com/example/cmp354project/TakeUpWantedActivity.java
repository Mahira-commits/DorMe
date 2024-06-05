package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class TakeUpWantedActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_addWantedActivity_email, tv_addWantedActivity_serviceTitle ,tv_addWantedActivity_dormBlock, tv_addWantedActivity_dormNumber;
    private EditText et_addWantedActivity_description, et_addWantedActivity_cost;
    private Button btn_addWantedActivity_add;
    private String serviceTitle = "";
    private String claim_email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_up_wanted);
        tv_addWantedActivity_email = (TextView) findViewById(R.id.tv_addWantedActivity_email);
        tv_addWantedActivity_serviceTitle = (TextView) findViewById(R.id.tv_addWantedActivity_serviceTitle);
        tv_addWantedActivity_dormBlock = (TextView) findViewById(R.id.tv_addWantedActivity_dormBlock);
        tv_addWantedActivity_dormNumber = (TextView) findViewById(R.id.tv_addWantedActivity_dormNumber);
        et_addWantedActivity_description = (EditText) findViewById(R.id.et_addWantedActivity_description);
        et_addWantedActivity_cost = (EditText) findViewById(R.id.et_addWantedActivity_cost);
        btn_addWantedActivity_add = (Button) findViewById(R.id.btn_addWantedActivity_add);

        btn_addWantedActivity_add.setOnClickListener(this);

        DocumentReference user = MainActivity.db.collection("UserProfiles").document(HomePage.current_user_email);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Intent intent = getIntent();
                        serviceTitle = intent.getStringExtra("Title");
                        claim_email = intent.getStringExtra("Claim Email");

                        tv_addWantedActivity_email.setText(document.getString("email") + "");
                        tv_addWantedActivity_serviceTitle.setText(serviceTitle);
                        tv_addWantedActivity_dormBlock.setText(document.getString("dormBlock") + "");
                        tv_addWantedActivity_dormNumber.setText(document.getString("dormNum") + "");
                        Log.d("AAAA", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("AAAA", "No such document");
                    }
                } else {
                    Log.d("AAAA", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_addWantedActivity_add)
        {
            Map<String, Object> service = new HashMap<>();
            service.put("OwnerEmail", tv_addWantedActivity_email.getText() + "");
            service.put("serviceTitle", tv_addWantedActivity_serviceTitle.getText() + "");
            service.put("desc", et_addWantedActivity_description.getText() + "");
            service.put("cost", et_addWantedActivity_cost.getText() + "");
            service.put("dormBlock", tv_addWantedActivity_dormBlock.getText() + "");
            service.put("dormNum", tv_addWantedActivity_dormNumber.getText() + "");
            service.put("status", "0");
            service.put("ClaimEmail", "");

            MainActivity.db.collection("dormServices").document(HomePage.current_user_email.split("@")[0] + "-" + tv_addWantedActivity_serviceTitle.getText())
                    .set(service)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "Service Added Sucessfully!" , Toast.LENGTH_SHORT).show();
                            Log.d("CCCC", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error Adding Service!" , Toast.LENGTH_SHORT).show();
                        }
                    });

            MainActivity.db.collection("dormServices").document("-" + tv_addWantedActivity_serviceTitle.getText())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(getApplicationContext(), "Service Deleted Suucessfully!" , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error Updating List!" , Toast.LENGTH_SHORT).show();
                        }
                    });

            startActivity(new Intent(getApplicationContext(), Services_I_Offer.class));
        }
    }
}