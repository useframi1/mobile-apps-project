package edu.aucegypt.gymwya;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {
    GridViewAdapter gridAdapter;

    ImageView profile_picture;
    TextView name;
    List<Sport> sportList;
    LinearLayout viewRequests;
    private DataManager dataManager;
    private Data dataModel;
    Button viewRequestsB;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intente = getIntent();
        String email = intente.getStringExtra("email");
        HomePage.GetUserTask createUserTask = new HomePage.GetUserTask();
        createUserTask.execute(email);
        setContentView(R.layout.activity_home_page);

        //retrieve email from prev screen


        name = findViewById(R.id.name);
        profile_picture = findViewById(R.id.profile_picture);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();


// Retrieve the image URL from Firestore based on the user's email
        db.collection("Images")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null) {
                            // Image URL retrieved successfully, load the image into ImageView
                            // You can use any image loading library or method here, for example, Glide or Picasso
                            // Here's an example using Glide:
                            Glide.with(this)
                                    .load(imageUrl)
                                    .apply(new RequestOptions())  // Optional: Add a placeholder image
                                    .into(profile_picture);
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
                    // Error occurred while retrieving the image URL from Firestore
                    // Handle the error accordingly
                });


        dataManager = DataManager.getInstance();
        dataModel = dataManager.getDataModel();
        dataModel.previousIsHome = true;

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
                i.putExtra("email", email);
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
    private class GetUserTask extends AsyncTask<String, Void, String> {

        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            OkHttpClient client = new OkHttpClient(); // Initialize the OkHttpClient

            try {
                // Create an HTTP request
                Request request = new Request.Builder()
                        .url("http://172.20.10.2:3000/getUsername?email=" + email)  // Replace with your actual URL
                        .get()
                        .build();


                // Send the request and get the response
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    // Iterate through the JSON array
                    StringBuilder usernameBuilder = new StringBuilder();
                        JSONObject jsonObject = jsonArray.getJSONObject(1);
                        String username = jsonObject.getString("username");




                    // Display the usernames in a Toast message
                    Toast.makeText(getApplicationContext(), "Usernames: " + username, Toast.LENGTH_SHORT).show();

                    // Set the usernames to the TextView
                    String finalUsernames = username;
                    name.post(() -> name.setText(finalUsernames));
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Failed to parse JSON response
                    Toast.makeText(getApplicationContext(), "Failed to parse JSON response", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Failed to retrieve username
                Toast.makeText(getApplicationContext(), "Failed to retrieve username", Toast.LENGTH_SHORT).show();
            }
        }

    }

}