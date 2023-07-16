package edu.aucegypt.gymwya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
//import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    GridViewAdapter gridAdapter;
    List<Sport> sportList;
    LinearLayout viewRequests;
    private Main dataManager;
    private SubMain dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        dataManager = Main.getInstance();
        dataModel = dataManager.getDataModel();

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            item.getIcon().setTint(Color.BLACK);
            Intent i = new Intent();
            if (item.getItemId() == R.id.home) {
                return false;
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, Chats.class);
            } else if (item.getItemId() == R.id.profile) {
                i = new Intent(this, Profile.class);
            }
            startActivity(i);
            return false;
        });

        Spinner create = findViewById(R.id.spinner);
        SearchView searchView = findViewById(R.id.searchView);
        viewRequests = findViewById(R.id.view_requests);

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
                    System.out.println("pressed");
                    startActivity(i);
                } else if (position == 2) {
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

        gridView.setOnItemClickListener((adapterView, view, position, id) -> {

            Intent intent;
            if (sportList.get(position).isIndividual)
                intent = new Intent(HomePage.this, IndividualMatching.class);
            else
                intent = new Intent(HomePage.this, GroupMatching.class);
            intent.putExtra("selectedSport", sportList.get(position).sportName);
            startActivity(intent);
        });

        viewRequests.setOnClickListener(v -> {
            Intent i = new Intent(this, ViewRequests.class);
            startActivity(i);
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
    }

    // Create the list of sports
    private List<Sport> createSportList() {
        List<Sport> sportList = new ArrayList<>();
        sportList.add(new Sport("Swimming",false, new Sport.SportIcon(R.drawable.swimming_icon)));
        sportList.add(new Sport("Gym", true, new Sport.SportIcon(R.drawable.gym_icon)));
        sportList.add(new Sport("Football", false, new Sport.SportIcon(R.drawable.football_icon)));
        sportList.add(new Sport("PingPong",true, new Sport.SportIcon(R.drawable.pingpong_icon)));
        sportList.add(new Sport("BasketBall",false, new Sport.SportIcon(R.drawable.basketball_icon)));
        sportList.add(new Sport("VolleyBall", false, new Sport.SportIcon(R.drawable.volleyball_icon)));
        sportList.add(new Sport("Tennis", true, new Sport.SportIcon(R.drawable.tennis_icon)));
        sportList.add(new Sport("Squash",true, new Sport.SportIcon(R.drawable.squash_icon)));
        return sportList;
    }
}