package com.example.trojan0project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RemoveProfileFragment.RemoveProfileDialogListener {

    private ArrayList<Profile> dataList;
    private ListView profileList;
    private ArrayAdapter<Profile> profileAdapter;

    public void removeProfile(Profile profile){
        if (profile != null){
            profileAdapter.remove(profile);
            profileAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        String[] profiles = { "Gurleen", "Farza", "Vishal", "Vivaan", "Charvi", "Jacob" };
        int[] profilePicture = {R.drawable.baseline_person_24,
                                R.drawable.baseline_person_24,
                                R.drawable.baseline_person_24,
                                R.drawable.baseline_person_24,
                                R.drawable.baseline_person_24,
                                R.drawable.baseline_person_24};
        dataList = new ArrayList<Profile>();
        for (int i = 0; i < profiles.length; i++){
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


        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Home Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Profile Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });
        ImageButton imageButton = findViewById(R.id.camera_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Camera Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        ImageButton eventsButton = findViewById(R.id.events_button);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Events Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}