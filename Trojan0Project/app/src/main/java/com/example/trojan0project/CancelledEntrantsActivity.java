package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CancelledEntrantsActivity extends AppCompatActivity {
    private static final String TAG = "CancelledEntrantsActivity";

    private FirebaseFirestore db;
    ListView cancelledEntrantsListView;
    private ArrayList<Profile> cancelledEntrantsList;
    private ArrayAdapter<Profile> adapter;

    private String eventId;
    //private String eventId = "KaAoFKkyEAyX5Hb3MKy8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_entrants);

        db = FirebaseFirestore.getInstance();

        cancelledEntrantsListView = findViewById(R.id.cancelledEntrantsListView);
        cancelledEntrantsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cancelledEntrantsList);
        cancelledEntrantsListView.setAdapter(adapter);


        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId != null) {
            fetchCancelledEntrants(eventId);
        } else {
            Toast.makeText(this, "Error: Event ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCancelledEntrants(String eventId) {
        final CollectionReference collectionReference = db.collection("users");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {


                Log.d("Waitlist", "onEvent triggered");

                cancelledEntrantsList.clear();


                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    String userType = doc.getString("user_type");
                    if ("entrant".equals(userType)){
                        Map<String, Long> events = (Map<String, Long>) doc.get("events");

                        if (events != null) {
                            for (Map.Entry<String, Long> entry : events.entrySet()) {

                                if (entry.getValue() == 3 && entry.getKey().equals(eventId)) {
                                    String eventId = entry.getKey(); // This is the event ID
                                    Log.d("CancelledEntrantsList", "Event ID with 3: " + eventId);
                                    String firstName = doc.getString("first_name");
                                    String lastName = doc.getString("last_name");
                                    String email = doc.getString("email");

                                    //create profile
                                    Profile profile = new Profile(firstName, lastName, email);
                                    cancelledEntrantsList.add(profile);
                                    Log.d("Waitlist", "Added Profile: " + profile.getFirstName() + " " + profile.getLastName());



                                }



                            }
                        }

                    }
                }
                adapter.notifyDataSetChanged();

                for (Profile profile : cancelledEntrantsList) {
                    Log.d("Waitlist", "First Name: " + profile.getFirstName() +
                            ", Last Name: " + profile.getLastName() +
                            ", Email: " + profile.getEmail());

                }
            }


        });
    }
}
