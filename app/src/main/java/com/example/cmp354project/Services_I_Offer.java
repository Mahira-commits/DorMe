package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class Services_I_Offer extends AppCompatActivity implements View.OnClickListener {

    private Button btn_offer_addNewService;

    private ListView listview_offer_services;
    public static List<dormService> list_offer_services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_ioffer);

        btn_offer_addNewService = (Button) findViewById(R.id.btn_offer_addNewService);
        listview_offer_services  = (ListView) findViewById(R.id.listview_offer_services);

        btn_offer_addNewService.setOnClickListener(this);
        listview_offer_services.setOnItemLongClickListener(offer_viewServicesListListener);

        offer_getAllServices(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        offer_getAllServices(1);
    }

    AdapterView.OnItemLongClickListener offer_viewServicesListListener = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, final long id)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Services_I_Offer.this);

            TextView c = (TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle);
            final String msg = c.getText().toString();
            builder.setTitle("Delete"); // title bar string
            builder.setMessage("Do you want to delete \"" + msg + "\"  ?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Delete",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int button) {

                            String ID = HomePage.current_user_email.split("@")[0];

                            MainActivity.db.collection("dormServices").document(ID + "-" + ((TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle)).getText().toString())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            list_offer_services.remove(pos);
                                            offer_UpdateDisplay(list_offer_services);
                                            Toast.makeText(getApplicationContext(), "Service Deleted Suucessfully!" , Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error Deleting Document!" , Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

            builder.show(); // display the Dialog

            return true;
        }
    };


    public void offer_getAllServices(int state){
        CollectionReference dormServices = MainActivity.db.collection("dormServices");
        dormServices.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    list_offer_services = new ArrayList<dormService>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        dormService ds = new dormService(document.getString("OwnerEmail"), document.getString("serviceTitle"),
                                document.getString("desc"), document.getString("cost"), document.getString("dormBlock"), document.getString("dormNum"), document.getString("status"), document.getString("ClaimEmail"));

                        if(ds.getOwnerEmail().equals(HomePage.current_user_email) && ds.getStatus().equals("0"))
                        {
                            list_offer_services.add(ds);
                        }
                    }

                    if (state == 1) {
                        offer_UpdateDisplay(list_offer_services);
                    }

                } else {
                    Log.d("CMP354 Project", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void offer_UpdateDisplay(List<dormService> display){


        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (dormService s : display) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("OwnerEmail", s.getOwnerEmail() + "");
            map.put("ServiceTitle", s.getServiceTitle() + "");
            map.put("Cost", s.getCost() + "");

            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.list_view;
        String[] from = {"OwnerEmail", "ServiceTitle", "Cost"};
        int[] to = {R.id.tv_listview_OwnerEmail, R.id.tv_listview_ServiceTitle, R.id.tv_listview_cost};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        listview_offer_services.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_offer_addNewService)
        {
            startActivity(new Intent(getApplicationContext(), AddNewActivity.class));
        }
    }
}