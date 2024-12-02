/**
 * Purpose:
 * Manages a list of facilities and allows administrators to view and delete facilities
 * Gets facilities from firestore and displayed in a list view
 *
 * Design Rationale:
 * Firestore used to get, display and update the list of facilities
 * Implements `DeleteFacilityFragment.DeleteFacilityDialogListener` to handle deletion of selected facilities
 * Dialog fragment for deletion confirmation
 *
 * Outstanding Issues:
 * Creates space when facility is deleted
 *
 */
package com.example.trojan0project;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    /**
     * Deletes the specified facility from Firestore and updates the UI.
     *
     * @param facility The facility to delete.
     */
    @Override
    public void deleteFacility(Facility facility) {
        if (selectedFacility != null) { //city is not null so that means the user clicked on an existing city
            //facilityAdminAdapter.remove(selectedFacility);
            //facilityAdminAdapter.notifyDataSetChanged();
            db.collection("users") // CHANGE TO USERS
                    .whereEqualTo("facilityName", selectedFacility.getFacilityName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            db.collection("users").document(document.getId())//CHANGE TO USERS
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

    /**
     * Handles the selection of menu items, specifically the "home" button (up navigation).
     * This method is called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Finish the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes the activity, sets up the ListView, and loads facility data from Firestore.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.facility_main);

        Toolbar toolbar = findViewById(R.id.browse_facilities_toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the action bar to be empty
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the "up" button
        }

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("users");

        String []facilities = {"Swimming Pool","Ice Rink", "Field", "Gym" }; //string array consisting of events which can be fed into ListView

        dataList = new ArrayList<Facility>(); // ArrayList which will contain the data (string array of events)
        facilityAdminList = findViewById(R.id.admin_facilities_list); //find reference to to the ListView and assign it to eventAdminList
        facilityAdminAdapter = new FacilityArrayAdapter(this, dataList); // link content file and  and datalist as well as pass id of textview in content.xml
        facilityAdminList.setAdapter(facilityAdminAdapter); // show each TextView in scrolling list form

        /**
         * Listener for real-time Firestore data changes. This listener listens for updates to the collection of documents
         * and updates the `dataList` with the facility names of users with the type "organizer".
         *
         * <p>On every update, the existing data in `dataList` is cleared. Then, for each document in the Firestore query snapshot,
         * it checks if the document has the field "user_type" set to "organizer". If the document contains a "facilityName",
         * that value is extracted and added to the `dataList`. If the "facilityName" field is missing, a log message is generated.</p>
         *
         * <p>This listener is useful for keeping the UI updated with the latest data from Firestore in real-time.</p>
         *
         * @param queryDocumentSnapshots The snapshot of documents retrieved from Firestore.
         * @param error Any error encountered while listening for changes (can be null if no error occurred).
         */
        // Listener for Firestore data
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,@Nullable FirebaseFirestoreException error) {

                dataList.clear(); // Clear the existing data

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String userType = doc.getString("user_type");
                    if ("organizer".equals(userType)) {
                        // Get the facility name
                        if (doc.contains("facilityName")) {
                            // Only access the facilityName if it exists
                            String facilityName = doc.getString("facilityName");

                            // Add the facility to the list
                            dataList.add(new Facility(facilityName));
                        } else {
                            // Optionally, log or handle the case where "facilityName" doesn't exist
                            Log.d(TAG, "facilityName field is missing for document: " + doc.getId());
                        }
                    }
                }
                facilityAdminAdapter.notifyDataSetChanged();
            }
        });

        /**
         * Sets an item click listener for the facility admin list. When an item in the list is clicked, it triggers the
         * deletion dialog for the selected facility.
         *
         * <p>The listener retrieves the selected facility from the data list based on the clicked position and creates
         * a new instance of the {@link DeleteFacilityFragment}, passing the selected facility to the fragment for deletion.</p>
         *
         * <p>This fragment is then displayed using {@link FragmentTransaction} to show the delete confirmation dialog.</p>
         *
         * @param adapterView The AdapterView where the item was clicked.
         * @param view The view within the AdapterView that was clicked.
         * @param i The position of the clicked item in the adapter.
         * @param l The row id of the clicked item (unused here).
         */
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