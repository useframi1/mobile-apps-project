package edu.aucegypt.gymwya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private GridViewAdapter gridAdapter;
    private List<ModelClass> sportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Button viewRequest = findViewById(R.id.viewRequest);
        Spinner create = findViewById(R.id.spinner);
        SearchView searchView = findViewById(R.id.searchView);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.add("");
        spinnerAdapter.add("Create Group");
        spinnerAdapter.add("Create Individual Meeting");
        create.setAdapter(spinnerAdapter);

        create.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                if (position == 1) {
                    i = new Intent(HomePage.this, CreateGroup.class);
                    startActivity(i);
                }
                else if(position == 2) {
                    i = new Intent(HomePage.this, CreateMeeting.class);
                    startActivity(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up the GridView
        GridView gridView = findViewById(R.id.gridView);
        sportList = createSportList();
        gridAdapter = new GridViewAdapter(this, sportList);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent;
                if (sportList.get(position).getIsIndividual())
                    intent = new Intent(HomePage.this,IndividualMatching.class);
                else
                    intent = new Intent(HomePage.this,GroupMatching.class);
                intent.putExtra("selectedSport", sportList.get(position).getSportName());
                startActivity(intent);
            }
        });

        // Set up the search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                gridAdapter.filterData(query);
                return false;
            }
        });

        viewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open another activity
            }
        });
    }

    // Create the list of sports
    private List<ModelClass> createSportList() {
        List<ModelClass> sportList = new ArrayList<>();
        sportList.add(new ModelClass("Swimming", R.drawable.swimming, false));
        sportList.add(new ModelClass("Gym", R.drawable.gym, true));
        sportList.add(new ModelClass("Football", R.drawable.football, false));
        sportList.add(new ModelClass("PingPong", R.drawable.pingpong, true));
        sportList.add(new ModelClass("BasketBall", R.drawable.basketball, false));
        sportList.add(new ModelClass("VolleyBall", R.drawable.volleyball, false));
        sportList.add(new ModelClass("Tennis", R.drawable.tennis, true));
        sportList.add(new ModelClass("Sqaush", R.drawable.squash, true));
        return sportList;
    }
}
