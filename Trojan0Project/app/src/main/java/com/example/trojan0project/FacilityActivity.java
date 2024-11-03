package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FacilityActivity extends AppCompatActivity implements DeleteFacilityFragment.DeleteFacilityDialogListener {

    private ListView facilityAdminList; //create reference to the Listview
    private ArrayAdapter<Facility> facilityAdminAdapter;
    private ArrayList<Facility> dataList;
    private Facility selectedFacility = null;


    //NEW
    @Override
    public void deleteFacility(Facility facility) {
        if (selectedFacility != null) { //city is not null so that means the user clicked on an existing city
            facilityAdminAdapter.remove(selectedFacility);
            facilityAdminAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.facility_main);



        String []facilities = {"Swimming Pool","Ice Rink", "Field", "Gym" }; //string array consisting of events which can be fed into ListView

        dataList = new ArrayList<Facility>(); // ArrayList which will contain the data (string array of events)
        for (int i = 0; i < facilities.length; i++) {
            dataList.add(new Facility(facilities[i]));

        }
        //dataList.addAll(Arrays.asList(events)); // add the data in string array to dataList
        facilityAdminList = findViewById(R.id.admin_facilities_list); //find reference to to the ListView and assign it to eventAdminList
        facilityAdminAdapter = new FacilityArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        facilityAdminList.setAdapter(facilityAdminAdapter); // show each TextView in scrolling list form

        facilityAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFacility = dataList.get(i);

                //OpenAI, (2024, October 26), "How do I create a dialog where i can delete the selected event?", ChatGPT
                DeleteFacilityFragment fragment = DeleteFacilityFragment.newInstance(selectedFacility); //creates a new instance of DeleteEventragment which is selectedEvent(this pops up the screen for udeleting the evnts)
                fragment.show(getSupportFragmentManager(), "Delete Facility");

            }
        });

    }


}