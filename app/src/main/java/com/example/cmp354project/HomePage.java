package com.example.cmp354project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePage extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private EditText et_hp_search;
    private Switch switch_hp_filter, switch_hp_dormBlock, switch_hp_cost;
    private Button btn_hp_filter, btn_hp_search;
    private CheckBox chb_hp_A, chb_hp_B, chb_hp_C, chb_hp_D,chb_hp_E, chb_hp_F;
    private SeekBar seekBar_hp_cost;

    private int count_switched = 0;
    private int cb_count = 0;

    private ListView listview_hp_services;
    public static List<dormService> list_services = new ArrayList<dormService>();

    public static String current_user_email;
    private List<dormService> filteredsearch;
    private MenuItem mi_services_I_offer, mi_services_I_have_taken, mi_services_wanted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        et_hp_search = (EditText) findViewById(R.id.et_hp_search);
        chb_hp_A = (CheckBox) findViewById(R.id.chb_hp_A);
        chb_hp_B = (CheckBox) findViewById(R.id.chb_hp_B);
        chb_hp_C = (CheckBox) findViewById(R.id.chb_hp_C);
        chb_hp_D = (CheckBox) findViewById(R.id.chb_hp_D);
        chb_hp_E = (CheckBox) findViewById(R.id.chb_hp_E);
        chb_hp_F = (CheckBox) findViewById(R.id.chb_hp_F);
        seekBar_hp_cost  = (SeekBar) findViewById(R.id.seekBar_hp_cost);;
        switch_hp_filter = (Switch) findViewById(R.id.switch_hp_filter);
        switch_hp_dormBlock = (Switch) findViewById(R.id.switch_hp_dormBlock);
        switch_hp_cost = (Switch) findViewById(R.id.switch_hp_cost);;
        btn_hp_filter = (Button) findViewById(R.id.btn_hp_filter);
        btn_hp_search = (Button) findViewById(R.id.btn_hp_search);
        mi_services_I_offer = (MenuItem) findViewById(R.id.mi_services_I_offer);
        mi_services_I_have_taken = (MenuItem) findViewById(R.id.mi_services_I_offer);
        mi_services_wanted = (MenuItem) findViewById(R.id.mi_services_I_offer);

        switch_hp_filter.setOnCheckedChangeListener(this);
        switch_hp_dormBlock.setOnCheckedChangeListener(this);
        switch_hp_cost.setOnCheckedChangeListener(this);;
        chb_hp_A.setOnCheckedChangeListener(this);
        chb_hp_B.setOnCheckedChangeListener(this);
        chb_hp_C.setOnCheckedChangeListener(this);
        chb_hp_D.setOnCheckedChangeListener(this);
        chb_hp_E.setOnCheckedChangeListener(this);
        chb_hp_F.setOnCheckedChangeListener(this);

        collapseCheckBoxes(true);
        switch_hp_dormBlock.setVisibility(View.GONE);
        switch_hp_cost.setVisibility(View.GONE);
        seekBar_hp_cost.setVisibility(View.GONE);
        btn_hp_filter.setVisibility(View.GONE);

        listview_hp_services  = (ListView) findViewById(R.id.listview_hp_services);
        listview_hp_services.setOnItemClickListener(viewServicesListListener);

        btn_hp_filter.setOnClickListener(this);
        btn_hp_search.setOnClickListener(this);

        Intent intent = getIntent();
        current_user_email = intent.getStringExtra("currentUserEmail");

        getAllServices(1);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllServices(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_services_I_offer: {
                startActivity(new Intent(getApplicationContext(), Services_I_Offer.class));
                return true;
            }
            case R.id.mi_services_I_have_taken: {
                startActivity(new Intent(getApplicationContext(), Services_I_Have_Taken.class));
                return true;
            }
            case R.id.mi_services_wanted: {
                startActivity(new Intent(getApplicationContext(), Services_Wanted.class));
                return true;
            }
            case R.id.mi_services_taken_from_me: {
                startActivity(new Intent(getApplicationContext(), Services_Taken_From_Me.class));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener viewServicesListListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, final long id)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);

            TextView c = (TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle);
            final String title = c.getText().toString();
            builder.setTitle("Employ"); // title bar string
            builder.setMessage("employ \"" + title + "\" Service ?");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int button) {

                            TextView d = (TextView) arg1.findViewById(R.id.tv_listview_OwnerEmail);
                            String ID = d.getText().toString().split("@")[0];

                            DocumentReference doc = MainActivity.db.collection("dormServices").document(ID + "-" + ((TextView) arg1.findViewById(R.id.tv_listview_ServiceTitle)).getText().toString());

                            doc.update("ClaimEmail", HomePage.current_user_email, "status", "1")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getAllServices(1);
                                            Log.d("AAAA", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("AAAA", "Error updating document", e);
                                        }
                                    });

                        }
                    });

            builder.show(); // display the Dialog

        }

    };


    public void getAllServices(int state){
        CollectionReference dormServices = MainActivity.db.collection("dormServices");
        dormServices.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    list_services = new ArrayList<dormService>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        dormService ds = new dormService(document.getString("OwnerEmail"), document.getString("serviceTitle"),
                                document.getString("desc"), document.getString("cost"), document.getString("dormBlock"), document.getString("dormNum"), document.getString("status"), document.getString("ClaimEmail"));

                        if(Integer.parseInt(ds.getStatus()) == 1 || ds.getOwnerEmail().equals(current_user_email) || Integer.parseInt(ds.getStatus()) == 2){

                        }
                        else{
                            list_services.add(ds);
                        }

                    }

                    if (state == 1) {
                        UpdateDisplay(list_services);
                    }

                } else {
                    Log.d("CMP354 Project", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    public void UpdateDisplay(List<dormService> display){


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
        listview_hp_services.setAdapter(adapter);



    }

    public void collapseAllSwitches(Boolean collapse)
    {
        if(collapse)
        {
            switch_hp_dormBlock.setVisibility(View.GONE);
            switch_hp_cost.setVisibility(View.GONE);
            btn_hp_filter.setVisibility(View.GONE);
            seekBar_hp_cost.setVisibility(View.GONE);
        }
        else
        {
            switch_hp_dormBlock.setVisibility(View.VISIBLE);
            switch_hp_cost.setVisibility(View.VISIBLE);
        }
    }

    public void collapseCheckBoxes(Boolean collapse)
    {
        if(collapse)
        {
            chb_hp_A.setVisibility(View.GONE);
            chb_hp_B.setVisibility(View.GONE);
            chb_hp_C.setVisibility(View.GONE);
            chb_hp_D.setVisibility(View.GONE);
            chb_hp_E.setVisibility(View.GONE);
            chb_hp_F.setVisibility(View.GONE);

            chb_hp_A.setChecked(false);
            chb_hp_B.setChecked(false);
            chb_hp_C.setChecked(false);
            chb_hp_D.setChecked(false);
            chb_hp_E.setChecked(false);
            chb_hp_F.setChecked(false);

        }
        else
        {
            chb_hp_A.setVisibility(View.VISIBLE);
            chb_hp_B.setVisibility(View.VISIBLE);
            chb_hp_C.setVisibility(View.VISIBLE);
            chb_hp_D.setVisibility(View.VISIBLE);
            chb_hp_E.setVisibility(View.VISIBLE);
            chb_hp_F.setVisibility(View.VISIBLE);
            btn_hp_filter.setVisibility(View.VISIBLE);
            cb_count = 0;
        }
    }

    public void show_filter_btn ()
    {
        Log.d("AAAA", "count_switched = " + count_switched);
        if(count_switched>0 || cb_count>0)
        {
            btn_hp_filter.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_hp_filter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.switch_hp_filter)
        {
            if(isChecked)
            {
                collapseAllSwitches(false);
            }
            else
            {
                switch_hp_dormBlock.setChecked(false);
                switch_hp_cost.setChecked(false);
                collapseAllSwitches(true);
                count_switched = 0;
                getAllServices(1);
            }
        }
        else if(buttonView.getId() == R.id.switch_hp_dormBlock)
        {
            if(isChecked)
            {
                collapseCheckBoxes(false);
            }
            else
            {
                cb_count = 0;
                collapseCheckBoxes(true);
            }
        }
        else if(buttonView.getId() == R.id.switch_hp_cost)
        {
            if(isChecked)
            {
                count_switched++;
                seekBar_hp_cost.setVisibility(View.VISIBLE);
                btn_hp_filter.setVisibility(View.VISIBLE);
            }
            else
            {
                count_switched--;
                seekBar_hp_cost.setVisibility(View.GONE);
            }
        }
        else if(buttonView.getId() == R.id.chb_hp_A || buttonView.getId() == R.id.chb_hp_B || buttonView.getId() == R.id.chb_hp_C || buttonView.getId() == R.id.chb_hp_D || buttonView.getId() == R.id.chb_hp_E || buttonView.getId() == R.id.chb_hp_F)
        {
            if(isChecked)
            {
                cb_count++;
                btn_hp_filter.setVisibility(View.VISIBLE);
            }
            else
            {
                cb_count--;
            }
        }

        show_filter_btn();
    }

    @Override
    public void onClick(View v) {


        switch(v.getId()){
            case R.id.btn_hp_filter:

                getAllServices(0);

                if (switch_hp_dormBlock.isChecked()){
                    List<dormService> filtering = new ArrayList<dormService>();
                    getAllServices(0);

                    for(dormService c : list_services)
                    {
                        filtering.add(c);
                    }
                    for (dormService s : filtering) {
                        switch(s.getDormBlock()){
                            case "A":
                                if(!chb_hp_A.isChecked()){
                                    list_services.remove(s);
                                }
                                break;
                            case "B":
                                if(!chb_hp_B.isChecked()){
                                    list_services.remove(s);
                                }
                                break;
                            case "C":
                                if(!chb_hp_C.isChecked()){
                                    list_services.remove(s);
                                }
                                break;
                            case "D":
                                if(!chb_hp_D.isChecked()){
                                    list_services.remove(s);
                                }
                                break;
                            case "E":
                                if(!chb_hp_E.isChecked()){
                                    list_services.remove(s);
                                }
                                break;
                            case "F":
                                if(!chb_hp_F.isChecked()){
                                    list_services.remove(s);
                                }
                                break;

                        }
                    }
                }



                if(switch_hp_cost.isChecked()) {

                    List<dormService> filtering2 = new ArrayList<dormService>();

                    for (dormService c : list_services) {
                        filtering2.add(c);
                    }

                    for (dormService s : filtering2) {
                        if (Integer.parseInt(s.getCost()) > seekBar_hp_cost.getProgress()) {
                            list_services.remove(s);
                        }

                    }


                }
                filteredsearch = list_services;
                UpdateDisplay(list_services);


                break;
            case R.id.btn_hp_search:
                if(et_hp_search.getText().length() == 0){
                    UpdateDisplay(filteredsearch);

                }
                else {
                    List<dormService> searchlist = new ArrayList<dormService>();
                    for (dormService s : filteredsearch) {
                        if(s.getServiceTitle().contains(et_hp_search.getText().toString())){
                            searchlist.add(s);
                        }

                    }
                    UpdateDisplay(searchlist);
                }
                break;
        }
    }

}
