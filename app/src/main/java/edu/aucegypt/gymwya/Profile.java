package edu.aucegypt.gymwya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile extends AppCompatActivity {
    Button editProfile;
    RecyclerView recyclerView;
    IconsAdapter mAdapter;
    LinearLayout currentMatches, createdMeetings, createdGroups;
    TextView username, fullName, bio;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.home) {
                i = new Intent(this, HomePage.class);
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, Chats.class);
            } else {
                return false;
            }
            startActivity(i);
            return true;
        });

        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new IconsAdapter(dataModel.currentUser.getPreferredSportsIcons(), true, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        editProfile = findViewById(R.id.edit_profile);
        createdMeetings = findViewById(R.id.view_created_meetings);
        createdGroups = findViewById(R.id.view_created_groups);
        username = findViewById(R.id.username);
        fullName = findViewById(R.id.full_name);
        bio = findViewById(R.id.bio);

        username.setText(dataModel.currentUser.username);
        fullName.setText(dataModel.currentUser.name);
        bio.setText(dataModel.currentUser.bio);

        editProfile.setOnClickListener(view -> {
            Intent intent= new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
        });

        createdMeetings.setOnClickListener(view-> {
            Intent intent = new Intent (Profile.this, ViewCreatedMeetings.class);
            startActivity(intent);
        });

        createdGroups.setOnClickListener(view -> {
            Intent intent = new Intent (Profile.this, ViewCreatedGroups.class);
            startActivity(intent);
        });
    }
}