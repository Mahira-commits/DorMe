package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Services_Wanted extends AppCompatActivity implements View.OnClickListener{

    private Button btn_wanted_add;

    private ListView listview_wanted_services;
    public static List<dormService> list_wanted_services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_wanted);

        btn_wanted_add = (Button) findViewById(R.id.btn_wanted_add);
        listview_wanted_services  = (ListView) findViewById(R.id.listview_wanted_services);

        btn_wanted_add.setOnClickListener(this);
        listview_wanted_services.setOnItemLongClickListener(wanted_viewServicesListListener);

        wanted_getAllServices(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wanted_getAllServices(1);
    }

    AdapterView.OnItemLongClickListener wanted_viewServicesListListener = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, final long id)
        {
            TextView c = (TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle);
            final String title = c.getText().toString();

            TextView d = (TextView) arg1.findViewById(R.id.tv_listview_OwnerEmail);
            final String claim_email = d.getText().toString();

            Intent intent = new Intent(getApplicationContext(), TakeUpWantedActivity.class);
            intent.putExtra("Title", title);
            intent.putExtra("Claim Email", claim_email);
            startActivity(intent);
            return true;
        }
    };


    public void wanted_getAllServices(int state){
        CollectionReference dormServices = MainActivity.db.collection("dormServices");
        dormServices.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    list_wanted_services = new ArrayList<dormService>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        dormService ds = new dormService(document.getString("OwnerEmail"), document.getString("serviceTitle"),
                                document.getString("desc"), document.getString("cost"), document.getString("dormBlock"), document.getString("dormNum"), document.getString("status"), document.getString("ClaimEmail"));

                        if(ds.getStatus().equals("2") && !(ds.getOwnerEmail().equals(HomePage.current_user_email)))
                        {
                            list_wanted_services.add(ds);
                        }
                    }

                    if (state == 1) {
                        wanted_UpdateDisplay(list_wanted_services);
                    }

                } else {
                    Log.d("CMP354 Project", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void wanted_UpdateDisplay(List<dormService> display){


        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (dormService s : display) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("OwnerEmail", s.getOwnerEmail() + ""); //Refers to the person who wants this service to be offered
            map.put("ServiceTitle", s.getServiceTitle() + "");
            map.put("Cost", s.getCost() + "");

            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.list_view;
        String[] from = {"ServiceTitle"};
        int[] to = {R.id.tv_listview_ServiceTitle};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        listview_wanted_services.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_wanted_add)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Services_Wanted.this);

            final EditText edittext = new EditText(Services_Wanted.this);
            builder.setMessage("Enter Service Title: ");
            builder.setTitle("Add New Service Wanted: ");

            builder.setView(edittext);
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Add",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int button) {
                            String serviceTitle = edittext.getText().toString();

                            Map<String, Object> service = new HashMap<>();
                            service.put("OwnerEmail", HomePage.current_user_email);
                            service.put("serviceTitle", serviceTitle);
                            service.put("desc", "");
                            service.put("cost", "");
                            service.put("dormBlock", "");
                            service.put("dormNum", "");
                            service.put("status", "2");
                            service.put("ClaimEmail", "");

                            MainActivity.db.collection("dormServices").document(HomePage.current_user_email + "-" + serviceTitle)
                                    .set(service)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Service Added Suucessfully!" , Toast.LENGTH_SHORT).show();
                                            wanted_getAllServices(1);
                                            Log.d("CCCC", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error Adding Service!" , Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

            builder.show(); // display the Dialog
        }
    }
}