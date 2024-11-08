package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FacilityActivity extends AppCompatActivity implements DeleteFacilityFragment.DeleteFacilityDialogListener {

    private ListView facilityAdminList; //create reference to the Listview
    private ArrayAdapter<Facility> facilityAdminAdapter;
    public ArrayList<Facility> dataList;
    private Facility selectedFacility = null;
    private FirebaseFirestore db;


    //NEW
    @Override
    public void deleteFacility(Facility facility) {
        if (selectedFacility != null) { //city is not null so that means the user clicked on an existing city
            //facilityAdminAdapter.remove(selectedFacility);
            //facilityAdminAdapter.notifyDataSetChanged();
            db.collection("organizers")
                    .whereEqualTo("facilityName", selectedFacility.getFacilityName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            db.collection("organizers").document(document.getId())
                                    .update("facilityName", FieldValue.delete())
                                    .addOnSuccessListener(Void ->{
                                        dataList.remove(selectedFacility);
                                        facilityAdminAdapter.notifyDataSetChanged();

                                        Toast.makeText(this, "Facility is deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Facility not deleted", Toast.LENGTH_SHORT).show());

                        }
                    })
                    .addOnFailureListener(e ->{
                        Toast.makeText(this, "Event not deleted", Toast.LENGTH_SHORT).show();
                    });
            facilityAdminAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.facility_main);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("users");



        String []facilities = {"Swimming Pool","Ice Rink", "Field", "Gym" }; //string array consisting of events which can be fed into ListView

        dataList = new ArrayList<Facility>(); // ArrayList which will contain the data (string array of events)
        //for (int i = 0; i < facilities.length; i++) {
            //dataList.add(new Facility(facilities[i]));

        //}
        //dataList.addAll(Arrays.asList(events)); // add the data in string array to dataList
        facilityAdminList = findViewById(R.id.admin_facilities_list); //find reference to to the ListView and assign it to eventAdminList
        facilityAdminAdapter = new FacilityArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        facilityAdminList.setAdapter(facilityAdminAdapter); // show each TextView in scrolling list form


        // Listener for Firestore data
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,@Nullable FirebaseFirestoreException error) {


                dataList.clear(); // Clear the existing data

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String userType = doc.getString("user_type");
                    if ("organizer".equals(userType)) {
                        // Get the facility name
                        String facilityName = doc.getString("facilityName");

                        dataList.add(new Facility(facilityName));

                    }
                }

                facilityAdminAdapter.notifyDataSetChanged();
            }
        });



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