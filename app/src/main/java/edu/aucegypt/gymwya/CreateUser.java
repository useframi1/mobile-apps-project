package edu.aucegypt.gymwya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateUser extends AppCompatActivity {
    private GridViewAdapter gridAdapter;
    private List<ModelClass> sportList;
    Button create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        create = findViewById(R.id.createUserButton);


        GridView gridView = findViewById(R.id.sport_list);
        sportList = createSportList();
        gridAdapter = new GridViewAdapter(this, sportList);

        gridAdapter.setConvertViewLayoutResource(R.layout.sports_list); // Change to the desired layout file

        gridView.setAdapter(gridAdapter);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUser.this, HomePage.class);
                startActivity(intent);
            }
        });
        
    }
    private List<ModelClass> createSportList() {
        List<ModelClass> sportList = new ArrayList<>();
        sportList.add(new ModelClass("Swimming", R.drawable.swimming_icon, false));
       // sportList.add(new ModelClass("Gym", R.drawable.gym, true));
        //sportList.add(new ModelClass("Football", R.drawable.football, false));
        sportList.add(new ModelClass("PingPong", R.drawable.pingpong_icon, true));
        sportList.add(new ModelClass("BasketBall", R.drawable.basketball_icon, false));
        sportList.add(new ModelClass("VolleyBall", R.drawable.volleyball_icon, false));
        sportList.add(new ModelClass("Tennis", R.drawable.tennis_icon, true));
        sportList.add(new ModelClass("Sqaush", R.drawable.squash_icon, true));
        return sportList;
    }
}
