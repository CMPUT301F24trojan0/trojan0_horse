package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements DeleteEventFragment.DeleteEventDialogListener {

    private ListView eventAdminList; //create reference to the Listview
    private ArrayAdapter<Event> eventAdminAdapter;
    private ArrayList<Event> dataList;
    private Event selectedEvent = null;
    private FirebaseFirestore db;


    //NEW

    @Override
    public void deleteQRCode(Event event) {
        selectedEvent.removeQRCode();
        eventAdminAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView


    }

    @Override
    public void deleteEvent(Event event) {
        if (selectedEvent != null) { //city is not null so that means the user clicked on an existing city
            //eventAdminAdapter.remove(selectedEvent);
            //eventAdminAdapter.notifyDataSetChanged();
            //delete from firestore db
            db.collection("events")
                    .whereEqualTo("name", selectedEvent.getEventName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            db.collection("events").document(document.getId()).delete()
                                    .addOnSuccessListener(Void ->{
                                        dataList.remove(selectedEvent);
                                        eventAdminAdapter.notifyDataSetChanged();
                                        Toast.makeText(this, "Event is deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show());

                        }
                    })
                    .addOnFailureListener(e ->{
                        Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show();
                    });


        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.events_main);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("events");


        String []events = {"Beginners Swimming","Golfing", "Baking classes", "Picnic" }; //string array consisting of events which can be fed into ListView
        int[] qrImages = {R.drawable.qr_code, R.drawable.qr_code,
                R.drawable.qr_code, R.drawable.qr_code}; // array conssiting of all the diff qr codes
        dataList = new ArrayList<Event>(); // ArrayList which will contain the data (string array of events)
        //for (int i = 0; i < events.length; i++) {
            //dataList.add(new Event(events[i],qrImages[i]));

        //}

        //dataList.addAll(Arrays.asList(events)); // add the data in string array to dataList
        eventAdminList = findViewById(R.id.admin_events_list); //find reference to to the ListView and assign it to eventAdminList
        eventAdminAdapter = new EventArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        eventAdminList.setAdapter(eventAdminAdapter); // show each TextView in scrolling list form

        // Listener for Firestore data
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,@Nullable  FirebaseFirestoreException error) {


                dataList.clear(); // Clear the existing data

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String eventName = (String) doc.getData().get("name");
                    String qrImageUrl = (String) doc.getData().get("qrCodeUrl"); // assuming we are using a url for the qrcode
                    int qrImageResId;
                    if (qrImageUrl != null) {
                        qrImageResId = getResources().getIdentifier(qrImageUrl, "drawable", getPackageName());

                    } else {
                        qrImageResId = R.drawable.qr_code;
                    }
                    dataList.add(new Event(eventName, qrImageResId));

                }

                eventAdminAdapter.notifyDataSetChanged();
            }
        });

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
