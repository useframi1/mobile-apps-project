package edu.aucegypt.gymwya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateUser extends AppCompatActivity {
    private GridViewAdapter gridAdapter;
    private List<ModelClass> sportList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);



        GridView gridView = findViewById(R.id.sport_list);
        sportList = createSportList();
        gridAdapter = new GridViewAdapter(this, sportList);

        gridAdapter.setConvertViewLayoutResource(R.layout.sports_list); // Change to the desired layout file

        gridView.setAdapter(gridAdapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                Intent intent;
//                if (sportList.get(position).getIsIndividual())
//                    intent = new Intent(HomePage.this,IndividualMatching.class);
//                else
//                    intent = new Intent(HomePage.this,GroupMatching.class);
//                intent.putExtra("selectedSport", sportList.get(position).getSportName());
//                startActivity(intent);
//            }
//        });

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
