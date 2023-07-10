package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GroupMatching extends AppCompatActivity{
    CustomAdapter adapter;
    ListView listView;
    ImageView backArrow, home,chat,profile;
    Button addGroup;

    SearchView searchView;

    TextView noResults, sportName;
    ArrayList<Integer> groupIcons = new ArrayList<>();

    ArrayList<String> groupNames = new ArrayList<>();
    ArrayList<String> groupMembers = new ArrayList<>();
    ArrayList<Integer> groupNumber = new ArrayList<>();
    ArrayList<String> groupTime = new ArrayList<>();

@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.group_matching);

    Bundle bundle = getIntent().getExtras();
    String sport = bundle.getString("selectedSport");

    listView = findViewById(R.id.groupsList);
    home = findViewById(R.id.home_icon);
    chat = findViewById(R.id.messages_icon);
    profile = findViewById(R.id.profile_icon);
    addGroup = findViewById(R.id.plus_button);
    noResults = findViewById(R.id.no_results);
    searchView = findViewById(R.id.searchView);
    sportName = findViewById(R.id.sport);
    sportName.setText("Join a " + sport + " team");

    groupIcons.add(R.drawable.volleyball);
    groupIcons.add(R.drawable.volley2);
    groupIcons.add(R.drawable.volley3);
    groupIcons.add(R.drawable.volley4);

    groupNames.add("Volley at 5");
    groupNames.add("Aqkwa Shabaka");
    groupNames.add("Point 3al Tayer");
    groupNames.add("Yala Beach Volley");

    groupMembers.add("Nour");
    groupMembers.add("Youssef");
    groupMembers.add("Mariam");
    groupMembers.add("Nadine");
    groupMembers.add("Dana");
    groupMembers.add("Omar");

    groupNumber.add(3);
    groupNumber.add(2);
    groupNumber.add(5);
    groupNumber.add(6);

    groupTime.add("5:00 PM");
    groupTime.add("6:30 PM");
    groupTime.add("9:00 PM");
    groupTime.add("3:00 PM");

    adapter = new CustomAdapter(getApplicationContext(), groupNames, groupIcons, groupMembers, groupNumber, groupTime);
    listView.setAdapter(adapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent(GroupMatching.this, IndividualMatching.class);
           // Intent intent = new Intent(GroupMatching.this, GroupInfo.class);
            String chosenGroup = groupNames.get(position);
            intent.putExtra("Team", chosenGroup);
            startActivity(intent);
        }
    });

    backArrow = (ImageView) findViewById(R.id.back_arrow2);
    backArrow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });

    addGroup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(GroupMatching.this, CreateGroup.class);
            startActivity(intent);
        }
    });

//    home.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(GroupMatching.this, HomePage.class);
//            startActivity(intent);
//        }
//    });

    //    chat.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(GroupMatching.this, ChatPage.class);
//            startActivity(intent);
//        }
//    });

    //    profile.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(GroupMatching.this, UserProfile.class);
//            startActivity(intent);
//        }
//    });

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            newText = newText.toLowerCase();
            ArrayList<String> newGroupNames = new ArrayList<>();
            ArrayList<Integer> newGroupIcons = new ArrayList<>();
            ArrayList<String> newGroupMembers = new ArrayList<>();
            ArrayList<Integer> newGroupNumber = new ArrayList<>();
            ArrayList<String> newGroupTime = new ArrayList<>();

            for (int i = 0; i < groupNames.size(); i++) {
                if (groupNames.get(i).toLowerCase().contains(newText)) {
                    newGroupNames.add(groupNames.get(i));
                    newGroupIcons.add(groupIcons.get(i));
                    newGroupMembers.add(groupMembers.get(i));
                    newGroupNumber.add(groupNumber.get(i));
                    newGroupTime.add(groupTime.get(i));
                }
            }
            if (newGroupNames.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            else {
                listView.setVisibility(View.VISIBLE);
                noResults.setVisibility(View.GONE);

                adapter = new CustomAdapter(getApplicationContext(), newGroupNames, newGroupIcons, groupMembers, newGroupNumber, newGroupTime);
                listView.setAdapter(adapter);
                hideKeyboard();
            }
            return false;
        }
    });
}
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
}
