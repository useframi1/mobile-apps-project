package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class GroupMatching extends AppCompatActivity{
    GroupMatchingAdapter adapter;
    ListView listView;
    ImageView backArrow;
    Button addGroup;
    SearchView searchView;
    TextView noResults, sportName;
    ArrayList<Group> groups = new ArrayList<>();
    private Main dataManager;
    private SubMain dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_matching);

        dataManager = Main.getInstance();
        dataModel = dataManager.getDataModel();
        dataModel.previousIsHome = false;

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.home) {
                i = new Intent(this, HomePage.class);
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, CreateGroup.class);
            } else {
                i = new Intent(this, Profile.class);
            }
            startActivity(i);
            return true;
        });

        Bundle bundle = getIntent().getExtras();
//        String sport = bundle.getString("selectedSport");


        groups.add(new Group("Volley at 5", R.drawable.group_icon1, 3, new ArrayList<>(), "2:30", "4:00"));
        groups.add(new Group("Aqkwa Shabaka", R.drawable.group_icon2, 2, new ArrayList<>(), "3:00", "5:00"));
        groups.add(new Group("Point 3al Tayer", R.drawable.group_icon3, 5, new ArrayList<>(), "12:30", "2:00"));
        groups.add(new Group("Yala Beach Volley", R.drawable.group_icon4, 6, new ArrayList<>(), "5:30", "7:00"));

        groups.get(0).members.add(new User("Nour", R.drawable.nour));
        groups.get(0).members.add(new User("Youssef", R.drawable.ghaleb));
        groups.get(0).members.add(new User("Mariam", R.drawable.mariam));

        groups.get(1).members.add(new User("Nadine", R.drawable.nadine));
        groups.get(1).members.add(new User("Dana", R.drawable.dana));

        groups.get(2).members.add(new User("Nour", R.drawable.nour));
        groups.get(2).members.add(new User("Youssef", R.drawable.ghaleb));
        groups.get(2).members.add(new User("Mariam", R.drawable.mariam));
        groups.get(2).members.add(new User("Nadine", R.drawable.nadine));
        groups.get(2).members.add(new User("Dana", R.drawable.dana));

        groups.get(3).members.add(new User("Nour", R.drawable.nour));
        groups.get(3).members.add(new User("Youssef", R.drawable.ghaleb));
        groups.get(3).members.add(new User("Mariam", R.drawable.mariam));
        groups.get(3).members.add(new User("Nadine", R.drawable.nadine));
        groups.get(3).members.add(new User("Dana", R.drawable.dana));
        groups.get(3).members.add(new User("Barbary", R.drawable.barbary));

        listView = findViewById(R.id.groupsList);
        adapter = new GroupMatchingAdapter(getApplicationContext(), groups);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(GroupMatching.this, edu.aucegypt.gymwya.ViewGroup.class);
            String chosenGroup = groups.get(position).name;
            intent.putExtra("Team", chosenGroup);
            Bundle temp = new Bundle();
            temp.putSerializable("Members", groups.get(position).members);
            intent.putExtras(temp);
            startActivity(intent);
        });

        backArrow = (ImageView) findViewById(R.id.back_arrow2);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupMatching.this, HomePage.class);
                startActivity(i);
            }
        });

        addGroup = findViewById(R.id.plus_button);
        addGroup.setOnClickListener(view -> {
            Intent intent = new Intent(GroupMatching.this, CreateGroup.class);
            startActivity(intent);
        });

        searchView = findViewById(R.id.searchView);
        noResults = findViewById(R.id.no_results);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<Group> groupsFound = new ArrayList<>();

                for (int i = 0; i < groups.size(); i++) {
                    if (groups.get(i).name.toLowerCase().contains(newText)) {
                        groupsFound.add(groups.get(i));
                    }
                }
                if (groupsFound.size() == 0) {
                    noResults.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    noResults.setVisibility(View.GONE);

                    adapter = new GroupMatchingAdapter(getApplicationContext(), groupsFound);
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

class GroupMatchingAdapter extends ArrayAdapter<Group> {

    public GroupMatchingAdapter(Context context, ArrayList<Group> groups) {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int totalGroupNumber;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.groups_list, parent, false);
        }

        ImageView teamImage = convertView.findViewById(R.id.team_image);
        TextView nameOfTeam = convertView.findViewById(R.id.team_name);
        TextView teamMember = convertView.findViewById(R.id.team_members);
        TextView teamNumber = convertView.findViewById(R.id.team_size);
        TextView teamSlot = convertView.findViewById(R.id.playing_time);

        Group group = getItem(position);

        teamImage.setImageResource(group.iconId);
        nameOfTeam.setText(group.name);

        StringBuilder membersBuilder = new StringBuilder();

        for (int i = 0; i < group.numberOfMembers; i++) {
            membersBuilder.append(group.members.get(i).name);
            if (i < group.numberOfMembers - 1) {
                membersBuilder.append(", ");
            }
        }

        if (membersBuilder.length() > 27) {
            membersBuilder.delete(27, membersBuilder.length());
            membersBuilder.append("...");
        }
        teamMember.setText(membersBuilder.toString());

        if (group.numberOfMembers < 6) {
            totalGroupNumber = 6;
        } else {
            totalGroupNumber = 12;
        }
        String capacity = group.numberOfMembers + "/" + totalGroupNumber;
        teamNumber.setText(capacity);
        String time = group.timeFrom + " to " + group.timeTo;
        teamSlot.setText(time);

        return convertView;
    }

}
