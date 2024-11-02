package com.example.trojan0project;

import android.os.Bundle;
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

    private ArrayList<Profile> dataList;
    private ListView profileList;
    private ProfileAdapter profileAdapter;
    private FirebaseFirestore db;


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
        getProfile();

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Profile selectedProfile = dataList.get(i);
                new RemoveProfileFragment(selectedProfile).show(getSupportFragmentManager(), "removeProfile");
            }
        });

    }
    private void getProfile(){
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String username = document.getString("username");
                        int profileImage = R.drawable.baseline_person_24;
                        dataList.add(new Profile(username, profileImage));
                    }
                    profileAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void removeProfile(Profile profile){
        db.collection("users")
                .whereEqualTo("username", profile.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        db.collection("users").document(document.getId()).delete()
                                .addOnSuccessListener(Void ->{
                                    dataList.remove(profile);
                                    profileAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Profile is deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Profile not deleted", Toast.LENGTH_SHORT).show());

                    }
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(this, "Profile not deleted", Toast.LENGTH_SHORT).show();
                });
    }



}

