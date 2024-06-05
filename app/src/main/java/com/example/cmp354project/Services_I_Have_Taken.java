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
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Services_I_Have_Taken extends AppCompatActivity implements View.OnClickListener {

    private ListView listview_taken_services;
    private List<dormService> list_taken_services = new ArrayList<dormService>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_ihave_taken);

        listview_taken_services  = (ListView) findViewById(R.id.listview_taken_services);
        listview_taken_services.setOnItemLongClickListener(taken_viewServicesListListener);

        taken_getAllServices(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taken_getAllServices(1);
    }

    AdapterView.OnItemLongClickListener taken_viewServicesListListener = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, final long id)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Services_I_Have_Taken.this);

            TextView c = (TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle);
            final String title = c.getText().toString();
            builder.setTitle("Completed"); // title bar string
            builder.setMessage("Mark \"" + title + "\" as completed ?");
            builder.setNegativeButton("Cancel Service",  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int button) {

                    TextView d = (TextView) arg1.findViewById(R.id.tv_listview_OwnerEmail);
                    String ID = d.getText().toString().split("@")[0];

                    DocumentReference doc = MainActivity.db.collection("dormServices").document(ID + "-" + ((TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle)).getText().toString());

                    doc.update("ClaimEmail", "", "status", "0")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    taken_getAllServices(1);
                                    Log.d("QQ", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("QQ", "Error updating document", e);
                                }
                            });

                }
            });
            builder.setNeutralButton("No", null);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int button) {

                            TextView d = (TextView) arg1.findViewById(R.id.tv_listview_OwnerEmail);
                            String ID = d.getText().toString().split("@")[0];

                            String owner = d.getText().toString();
                            String claim = HomePage.current_user_email;
                            int cost = Integer.parseInt(((TextView)arg1.findViewById(R.id.tv_listview_cost)).getText().toString());

                            DocumentReference Ouser = MainActivity.db.collection("userProfiles").document(owner);
                            Ouser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("QQQQ", "getting ownser success");

                                            Ouser.update("points", Integer.toString(Integer.parseInt(document.getString("points")) + cost))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("QQ", "Owner transaction Successful");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("QQ", "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            Log.d("QQQQ", "No such document");
                                        }
                                    } else {
                                        Log.d("QQQ", "get failed with ", task.getException());
                                    }
                                }
                            });

                            DocumentReference Cuser = MainActivity.db.collection("userProfiles").document(claim);
                            Cuser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("QQQQ", "getting ownser success");

                                            Cuser.update("points", Integer.toString(Integer.parseInt(document.getString("points")) - cost))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("QQ", "Owner transaction Successful");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("QQ", "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            Log.d("QQQQ", "No such document");
                                        }
                                    } else {
                                        Log.d("QQQ", "get failed with ", task.getException());
                                    }
                                }
                            });


                            MainActivity.db.collection("dormServices").document(ID + "-" + ((TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle)).getText().toString())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            builder.setMessage("Mark \"" + title + "\" by " +  "as completed ?");
                                            list_taken_services.remove(pos);
                                            taken_UpdateDisplay(list_taken_services);
                                            Toast.makeText(getApplicationContext(), "Service Marked as Completed Successfully!" , Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error Marking Service as Completed!" , Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

            builder.show(); // display the Dialog

            return true;
        }
    };


    public void taken_getAllServices(int state){
        CollectionReference dormServices = MainActivity.db.collection("dormServices");
        dormServices.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    list_taken_services = new ArrayList<dormService>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        dormService ds = new dormService(document.getString("OwnerEmail"), document.getString("serviceTitle"),
                                document.getString("desc"), document.getString("cost"), document.getString("dormBlock"), document.getString("dormNum"), document.getString("status"), document.getString("ClaimEmail"));

                        if(ds.getClaimEmail().equals(HomePage.current_user_email) && ds.getStatus().equals("1"))
                        {
                            list_taken_services.add(ds);
                        }
                    }

                    if (state == 1) {
                        taken_UpdateDisplay(list_taken_services);
                    }

                } else {
                    Log.d("CMP354 Project", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void taken_UpdateDisplay(List<dormService> display){


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
        listview_taken_services.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_offer_addNewService)
        {
            startActivity(new Intent(getApplicationContext(), AddNewActivity.class));
        }
    }
}