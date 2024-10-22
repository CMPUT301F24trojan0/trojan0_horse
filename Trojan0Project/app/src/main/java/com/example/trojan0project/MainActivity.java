package com.example.admin_project_part3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ListView eventAdminList; //create reference to the Listview
    ArrayAdapter<Event> eventAdminAdapter;
    ArrayList<Event> dataList;
    Event selectedEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        eventAdminList = findViewById(R.id.admin_events_list); //find reference to to the ListView and assign it to eventAdminList

        String []events = {"Beginners Swimming","Golfing", "Baking classes", "Picnic" }; //string array consisting of events which can be fed into ListView

        dataList = new ArrayList<Event>(); // ArrayList which will contain the data (string array of events)
        for (int i = 0; i < events.length; i++) {
            dataList.add(new Event(events[i]));

        }
        //dataList.addAll(Arrays.asList(events)); // add the data in string array to dataList
        eventAdminAdapter = new EventArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        eventAdminList.setAdapter(eventAdminAdapter); // show each TextView in scrolling list form

        eventAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEvent = dataList.get(i);
            }
        });

    }


}