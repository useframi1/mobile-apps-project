package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateUser extends AppCompatActivity {
    private GridViewAdapter gridAdapter;
    private List<Sport> sportList;
    Button create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        create = findViewById(R.id.createUserButton);


        GridView gridView = findViewById(R.id.sport_list);
        sportList = createSportList();
        gridAdapter = new GridViewAdapter(this, sportList);

        gridView.setAdapter(gridAdapter);

        create.setOnClickListener(v -> {
            Intent intent = new Intent(CreateUser.this, HomePage.class);
            startActivity(intent);
        });
        
    }
    private List<Sport> createSportList() {
        List<Sport> sportList = new ArrayList<>();
        sportList.add(new Sport("Swimming",false, new Sport.SportIcon(R.drawable.swimming_icon,false)));
        sportList.add(new Sport("Gym", true, new Sport.SportIcon(R.drawable.gym, false)));
        sportList.add(new Sport("Football", false, new Sport.SportIcon(R.drawable.football, false)));
        sportList.add(new Sport("PingPong",true, new Sport.SportIcon(R.drawable.pingpong_icon,false)));
        sportList.add(new Sport("BasketBall",false, new Sport.SportIcon(R.drawable.basketball_icon,false)));
        sportList.add(new Sport("VolleyBall", false, new Sport.SportIcon(R.drawable.volleyball_icon,false)));
        sportList.add(new Sport("Tennis", true, new Sport.SportIcon(R.drawable.tennis_icon,false)));
        sportList.add(new Sport("Squash",true, new Sport.SportIcon(R.drawable.squash_icon,false)));
        return sportList;
    }
}