package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewActivity extends AppCompatActivity implements View.OnClickListener  {

    private TextView tv_addActivity_email, tv_addActivity_dormBlock, tv_addActivity_dormNumber;
    private EditText et_addActivity_serviceTitle, et_addActivity_description, et_addActivity_cost;
    private Button btn_addActivity_add;
    private DocumentSnapshot userdoc;
    private List<String> user_serviceTitles_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        tv_addActivity_email = (TextView) findViewById(R.id.tv_addWantedActivity_email);
        tv_addActivity_dormBlock = (TextView) findViewById(R.id.tv_addWantedActivity_dormBlock);
        tv_addActivity_dormNumber = (TextView) findViewById(R.id.tv_addWantedActivity_dormNumber);
        et_addActivity_serviceTitle = (EditText) findViewById(R.id.et_addActivity_serviceTitle);
        et_addActivity_description = (EditText) findViewById(R.id.et_addActivity_description);
        et_addActivity_cost = (EditText) findViewById(R.id.et_addWantedActivity_cost);
        btn_addActivity_add = (Button) findViewById(R.id.btn_addWantedActivity_add);

        btn_addActivity_add.setOnClickListener(this);

        DocumentReference user = MainActivity.db.collection("UserProfiles").document(HomePage.current_user_email);
                user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            userdoc = document;
                            if (document.exists()) {
                                tv_addActivity_email.setText(document.getString("email") + "");
                                tv_addActivity_dormBlock.setText(document.getString("dormBlock") + "");
                                tv_addActivity_dormNumber.setText(document.getString("dormNum") + "");
                                Log.d("AAAA", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("AAAA", "No such document");
                            }
                        } else {
                            Log.d("AAAA", "get failed with ", task.getException());
                        }
                    }
                });

        user_serviceTitles_list = new ArrayList<String>();

        CollectionReference dormServices = MainActivity.db.collection("dormServices");
        dormServices.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("OwnerEmail").equals(HomePage.current_user_email))
                        {
                            user_serviceTitles_list.add(document.getString("serviceTitle"));
                        }
                    }



                } else {
                    Log.d("CMP354 Project", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_addWantedActivity_add)
        {
            if (et_addActivity_serviceTitle.getText().toString().isEmpty()) {
                et_addActivity_serviceTitle.setError("Service Name is Required!");
                return;
            }


           for (String s : user_serviceTitles_list){
               if(s.equals(et_addActivity_serviceTitle.getText())){
                   et_addActivity_serviceTitle.setError("You Already Offer This Service!");
                   return;
               }
           }


            if (et_addActivity_description.getText().toString().isEmpty()) {
                et_addActivity_description.setError("Description is required!");
                return;
            }

            if (et_addActivity_cost.getText().toString().isEmpty()) {
                et_addActivity_cost.setError("Cost is required!");
                return;
            }



            if(Integer.parseInt(userdoc.getString("points")) <  Integer.parseInt(et_addActivity_cost.getText().toString())){
                et_addActivity_cost.setError("You do not have this amount!");
                return;
            }

            Map<String, Object> service = new HashMap<>();
            service.put("OwnerEmail", HomePage.current_user_email);
            service.put("serviceTitle", et_addActivity_serviceTitle.getText() + "");
            service.put("desc", et_addActivity_description.getText() + "");
            service.put("cost", et_addActivity_cost.getText() + "");
            service.put("dormBlock", tv_addActivity_dormBlock.getText() + "");
            service.put("dormNum", tv_addActivity_dormNumber.getText() + "");
            service.put("status", "0");
            service.put("ClaimEmail", "");

            MainActivity.db.collection("dormServices").document(HomePage.current_user_email.split("@")[0] + "-" + et_addActivity_serviceTitle.getText())
                    .set(service)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Service Added Suucessfully!" , Toast.LENGTH_SHORT).show();
                            Log.d("CCCC", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error Adding Service!" , Toast.LENGTH_SHORT).show();
                        }
                    });

            et_addActivity_serviceTitle.setText("");
            et_addActivity_description.setText("");
            et_addActivity_cost.setText("");
            et_addActivity_serviceTitle.setHint("[Service Title]");
            et_addActivity_description.setHint("[Description]");
            et_addActivity_cost.setHint("0");
        }
    }
}