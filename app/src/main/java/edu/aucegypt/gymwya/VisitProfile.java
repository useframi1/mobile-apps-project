package edu.aucegypt.gymwya;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VisitProfile extends AppCompatActivity {
    Button message;
    ImageView back, pic;
    TextView name,username,age,bio;

    ArrayList<Sport.SportIcon> sport_icons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_profile);

        back = findViewById(R.id.back);
        pic = findViewById(R.id.profile_picture);
        message = findViewById(R.id.message);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        age = findViewById(R.id.age);
        bio = findViewById(R.id.bio);

        sport_icons.add(new Sport.SportIcon(R.drawable.football_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.volleyball_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.tennis_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.squash_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.basketball_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.swimming_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.pingpong_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.gym_icon));


        // Set up RecyclerView
        RecyclerView iconRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        iconRecyclerView.setLayoutManager(layoutManager);

// Create and set the adapter
        IconsAdapter iconAdapter = new IconsAdapter(sport_icons, false, false);
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