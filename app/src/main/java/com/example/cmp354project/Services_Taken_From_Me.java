package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Services_Taken_From_Me extends AppCompatActivity {

    private ListView listview_fromMe_services;
    private List<dormService> list_fromMe_services = new ArrayList<dormService>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_taken_from_me);

        listview_fromMe_services  = (ListView) findViewById(R.id.listview_fromMe_services);
        fromMe_getAllServices(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fromMe_getAllServices(1);
    }

    public void fromMe_getAllServices(int state){
        CollectionReference dormServices = MainActivity.db.collection("dormServices");
        dormServices.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    list_fromMe_services = new ArrayList<dormService>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        dormService ds = new dormService(document.getString("OwnerEmail"), document.getString("serviceTitle"),
                                document.getString("desc"), document.getString("cost"), document.getString("dormBlock"), document.getString("dormNum"), document.getString("status"), document.getString("ClaimEmail"));

                        if(ds.getOwnerEmail().equals(HomePage.current_user_email) && ds.getStatus().equals("1"))
                        {
                            list_fromMe_services.add(ds);
                        }
                    }

                    if (state == 1) {
                        fromMe_UpdateDisplay(list_fromMe_services);
                    }

                } else {
                    Log.d("CMP354 Project", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void fromMe_UpdateDisplay(List<dormService> display){


        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (dormService s : display) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("OwnerEmail", s.getClaimEmail() + "");
            map.put("ServiceTitle", s.getServiceTitle() + "");
            map.put("Cost", s.getCost() + "");

            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.list_view;
        //+++++++++++++++++ OWNER EMAIL HERE REFERS TO CLAIMER EMAIL!!! ++++++++++++++++++++++++++++++++++++
        String[] from = {"OwnerEmail", "ServiceTitle", "Cost"};
        int[] to = {R.id.tv_listview_OwnerEmail, R.id.tv_listview_ServiceTitle, R.id.tv_listview_cost};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        listview_fromMe_services.setAdapter(adapter);
    }
}