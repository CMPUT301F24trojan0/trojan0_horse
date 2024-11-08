/**
 * Purpose:
 * This loads profiles from Firebase and displays them in a list. This way admin can click on any
 * profile which opens a dialog and chooses to delete it.
 *
 * Design Rationale:
 * This uses a ProfileAdapter to display the profile list and separates the profile, display, and delete functions.
 * It also uses RemoveProfileFragment to ask to confirm before deleting a profile.
 *
 * Outstanding Issues:
 * If someone has the same username then all will be deleted
 */
package com.example.trojan0project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseProfileAdmin extends MainActivity implements RemoveProfileFragment.RemoveProfileDialogListener {

    public ArrayList<Profile> dataList;
    private ListView profileList;
    private ProfileAdapter profileAdapter;
    private FirebaseFirestore db;
    private String deviceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_profile_admin);

        db = FirebaseFirestore.getInstance();
        profileList = findViewById(R.id.profile_list);
        dataList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(this, dataList);
        profileList.setAdapter(profileAdapter);
        deviceId = getIntent().getStringExtra("DEVICE_ID");

        getProfile();

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Profile selectedProfile = dataList.get(i);
                new RemoveProfileFragment(selectedProfile).show(getSupportFragmentManager(), "removeProfile");
            }
        });

    }

    private void getProfile() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userType = document.getString("user_type");
                        if ("entrant".equals(userType)) {
                            String username = document.getString("username");
                            String profileImage = document.getString("profile_url");
                            dataList.add(new Profile(username, profileImage));

                        }
                    }
                    profileAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void removeProfile(Profile profile) {
        db.collection("users")
                .document(deviceId)
                .delete()
                .addOnSuccessListener(Void -> {
                    dataList.remove(profile);
                    profileAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Profile is deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Profile not deleted", Toast.LENGTH_SHORT).show();
                });
    }
}






