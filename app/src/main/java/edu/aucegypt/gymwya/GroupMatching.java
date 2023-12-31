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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GroupMatching extends AppCompatActivity{
    GroupMatchingAdapter adapter;
    ListView listView;
    ImageView backArrow;
    Button addGroup;
    SearchView searchView;
    TextView noResults, sport;
    private DataManager dataManager;
    private Data dataModel;
    String selectedSport = "";
    ArrayList<GroupMeeting> groups = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_matching);

        dataManager = DataManager.getInstance();
        dataModel = dataManager.getDataModel();
        dataModel.previousIsHome = false;

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
            selectedSport = bundle.getString("selectedSport");

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.home) {
                i = new Intent(this, HomePage.class);
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, Chats.class);
            } else {
                i = new Intent(this, Profile.class);
            }
            startActivity(i);
            return true;
        });

        for (int i = 0; i < dataModel.groupMeetings.size(); i++) {
            if (dataModel.groupMeetings.get(i).sport.equalsIgnoreCase(selectedSport) && !dataModel.groupMeetings.get(i).currentUserJoined)
                groups.add(dataModel.groupMeetings.get(i));
        }


        listView = findViewById(R.id.groupsList);
        adapter = new GroupMatchingAdapter(getApplicationContext(), groups);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(GroupMatching.this, edu.aucegypt.gymwya.ViewGroup.class);
            intent.putExtra("Group", groups.get(position));
            intent.putExtra("isInProfile", false);
            startActivity(intent);
        });

        backArrow = (ImageView) findViewById(R.id.back_arrow2);
        sport = findViewById(R.id.sport);
        sport.setText("Join a " + selectedSport + " Group");
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
            intent.putExtra("selectedSport", selectedSport);
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
                ArrayList<GroupMeeting> groupsFound = new ArrayList<>();

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

class GroupMatchingAdapter extends ArrayAdapter<GroupMeeting> {
    ArrayList<GroupMeeting> groups;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    public GroupMatchingAdapter(Context context, ArrayList<GroupMeeting> groups) {
        super(context, 0, groups);
        this.groups = groups;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.groups_list, parent, false);
        }

        ImageView teamImage = convertView.findViewById(R.id.group_picture);
        TextView nameOfTeam = convertView.findViewById(R.id.group_name);
        TextView from = convertView.findViewById(R.id.from);
        TextView to = convertView.findViewById(R.id.to);
        TextView date = convertView.findViewById(R.id.date);
        TextView sport = convertView.findViewById(R.id.sport);
        GroupMeeting group = getItem(position);
        db.collection("Images")
                .document(group.name)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null) {
                            // Image URL retrieved successfully, load the image into ImageView
                            // You can use any image loading library or method here, for example, Glide or Picasso
                            // Here's an example using Glide:
                            Glide.with(this.getContext())
                                    .load(imageUrl)
                                    .apply(new RequestOptions())  // Optional: Add a placeholder image
                                    .into(teamImage);
                        } else {
                            // Image URL not found in Firestore
                            // Handle the case accordingly
                        }
                    } else {
                        // Document not found in Firestore
                        // Handle the case accordingly
                    }
                })
                .addOnFailureListener(e -> {
                    // print the error if any in a toast message

                    group.name = e.getMessage();
                });

//        teamImage.setImageResource(group.iconId);
        nameOfTeam.setText(group.name);
        from.setText(group.start);
        to.setText(group.end);
        date.setText(group.date.substring(0,10));
        String firstChar = group.sport.substring(0, 1).toUpperCase();
        String restOfString = group.sport.substring(1).toLowerCase();
        sport.setText(firstChar+restOfString);
        return convertView;
    }

}
