package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VisitProfile extends AppCompatActivity {
    Button message;
    ImageView back, pic;
    TextView name,username,age,bio;

    ArrayList<Integer> sport_icons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_profile);

        back = findViewById(R.id.back);
        pic = findViewById(R.id.profile_picture);
        message = findViewById(R.id.message);
        name = findViewById(R.id.profileName);
        username = findViewById(R.id.profileUserName);
        age = findViewById(R.id.profileAge);
        bio = findViewById(R.id.profileBio);

        sport_icons.add(R.drawable.basketball);
        sport_icons.add(R.drawable.football);
        sport_icons.add(R.drawable.volleyball);
        sport_icons.add(R.drawable.tennis);
        sport_icons.add(R.drawable.pingpong);
        sport_icons.add(R.drawable.gym);
        sport_icons.add(R.drawable.swimming);


        // Set up RecyclerView
        RecyclerView iconRecyclerView = findViewById(R.id.iconRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        iconRecyclerView.setLayoutManager(layoutManager);

// Create and set the adapter
        IconAdapter iconAdapter = new IconAdapter(VisitProfile.this, sport_icons);
        iconRecyclerView.setAdapter(iconAdapter);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

//        message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(VisitProfile.this, Chat.class);
//                startActivity(i);
//            }
//        });
    }
}
