package com.example.trojan0project;

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

public class BrowseProfileAdmin extends MainActivity implements RemoveProfileFragment.RemoveProfileDialogListener {

    private ArrayList<Profile> dataList;
    private ListView profileList;
    private ArrayAdapter<Profile> profileAdapter;

    public void removeProfile(Profile profile) {
        if (profile != null) {
            profileAdapter.remove(profile);
            profileAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_profile_admin);


        String[] profiles = {"Gurleen", "Farza", "Vishal", "Vivaan", "Charvi", "Jacob"};
        int[] profilePicture = {R.drawable.baseline_person_24,
                R.drawable.baseline_person_24,
                R.drawable.baseline_person_24,
                R.drawable.baseline_person_24,
                R.drawable.baseline_person_24,
                R.drawable.baseline_person_24};
        dataList = new ArrayList<Profile>();
        for (int i = 0; i < profiles.length; i++) {
            dataList.add(new Profile(profiles[i], profilePicture[i]));
        }
        profileList = findViewById(R.id.profile_list);

        profileAdapter = new ProfileAdapter(this, dataList);

        profileList.setAdapter(profileAdapter);

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Profile selectedProfile = dataList.get(i);
                new RemoveProfileFragment(selectedProfile).show(getSupportFragmentManager(), "removeProfile");
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

