package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements DeleteEventFragment.DeleteEventDialogListener {

    private ListView eventAdminList; //create reference to the Listview
    private ArrayAdapter<Event> eventAdminAdapter;
    private ArrayList<Event> dataList;
    private Event selectedEvent = null;


    //NEW

    @Override
    public void deleteQRCode(Event event) {
        selectedEvent.removeQRCode();
        eventAdminAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
    }

    @Override
    public void deleteEvent(Event event) {
        if (selectedEvent != null) { //city is not null so that means the user clicked on an existing city
            eventAdminAdapter.remove(selectedEvent);
            eventAdminAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.events_main);



        String []events = {"Beginners Swimming","Golfing", "Baking classes", "Picnic" }; //string array consisting of events which can be fed into ListView
        int[] qrImages = {R.drawable.qr_code, R.drawable.qr_code,
                R.drawable.qr_code, R.drawable.qr_code}; // array conssiting of all the diff qr codes

        dataList = new ArrayList<Event>(); // ArrayList which will contain the data (string array of events)
        for (int i = 0; i < events.length; i++) {
            dataList.add(new Event(events[i],qrImages[i]));

        }
        //dataList.addAll(Arrays.asList(events)); // add the data in string array to dataList
        eventAdminList = findViewById(R.id.admin_events_list); //find reference to to the ListView and assign it to eventAdminList
        eventAdminAdapter = new EventArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        eventAdminList.setAdapter(eventAdminAdapter); // show each TextView in scrolling list form

        eventAdminList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEvent = dataList.get(i);

                //OpenAI, (2024, October 26), "How do I create a dialog where i can delete the selected event?", ChatGPT
                DeleteEventFragment fragment = DeleteEventFragment.newInstance(selectedEvent); //creates a new instance of DeleteEventragment which is selectedEvent(this pops up the screen for udeleting the evnts)
                fragment.show(getSupportFragmentManager(), "Delete Event");

            }
        });

    }


}
