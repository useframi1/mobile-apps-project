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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {
    GridViewAdapter gridAdapter;

    ImageView profile_picture;
    TextView name, viewRequestsCount;
    List<Sport> sportList;
    LinearLayout viewRequests;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    Spinner create;
    SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // retrieve email from prev screen

        name = findViewById(R.id.name);
        profile_picture = findViewById(R.id.profile_picture);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        name.setText(dataModel.currentUser.username);
        // Retrieve the image URL from Firestore based on the user's email
        // db.collection("Images")
        // .document(dataModel.currentUser.email)
        // .get()
        // .addOnSuccessListener(documentSnapshot -> {
        // if (documentSnapshot.exists()) {
        // String imageUrl = documentSnapshot.getString("image");
        // if (imageUrl != null) {
        // // Image URL retrieved successfully, load the image into ImageView
        // // You can use any image loading library or method here, for example, Glide
        // or Picasso
        // // Here's an example using Glide:
        // Glide.with(this)
        // .load(imageUrl)
        // .apply(new RequestOptions()) // Optional: Add a placeholder image
        // .into(profile_picture);
        // } else {
        // // Image URL not found in Firestore
        // // Handle the case accordingly
        // }
        // } else {
        // // Document not found in Firestore
        // // Handle the case accordingly
        // }
        // })
        // .addOnFailureListener(e -> {
        // // Error occurred while retrieving the image URL from Firestore
        // // Handle the error accordingly
        // });

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
            }
            startActivity(i);
            return false;
        });

        create = findViewById(R.id.spinner);
        SearchView searchView = findViewById(R.id.searchView);
        viewRequests = findViewById(R.id.view_requests);
        viewRequestsCount = findViewById(R.id.view_requests_count);

        if (dataModel.currentUser.unseenRequests > 0) {
            viewRequestsCount.setText(String.valueOf(dataModel.currentUser.unseenRequests));
            viewRequestsCount.setVisibility(View.VISIBLE);
        }

        ArrayList<String> menuTxt = new ArrayList<>();
        menuTxt.add("");
        menuTxt.add("Create group");
        menuTxt.add("Create Individual meeting");

        spinnerAdapter = new SpinnerAdapter(this, menuTxt);
        create.setAdapter(spinnerAdapter);
        int offsetInPixels = getResources().getDimensionPixelSize(R.dimen.dropdown_offset_create);
        create.setDropDownVerticalOffset(offsetInPixels);
        create.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                if (position == 1) {
                    i = new Intent(HomePage.this, CreateGroup.class);
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

            Intent i;
            if (sportList.get(position).isIndividual)
                i = new Intent(HomePage.this, IndividualMatching.class);
            else
                i = new Intent(HomePage.this, GroupMatching.class);
            i.putExtra("selectedSport", sportList.get(position).sportName);
            startActivity(i);
        });

        viewRequests.setOnClickListener(v -> {
            viewRequestsCount.setVisibility(View.GONE);

            JSONObject jsonData = new JSONObject();
            String jsonString = jsonData.toString();

            PostRequestsStatus postRequestsStatus = new PostRequestsStatus("http://192.168.1.182:3000/requestStatus",
                    jsonString);
            postRequestsStatus.execute();

            Intent i = new Intent(this, ViewRequests.class);
            startActivity(i);
            dataModel.currentUser.unseenRequests = 0;
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
        sportList.add(new Sport("Swimming", false, new Sport.SportIcon(R.drawable.swimming_icon)));
        sportList.add(new Sport("Gym", true, new Sport.SportIcon(R.drawable.gym_icon)));
        sportList.add(new Sport("Football", false, new Sport.SportIcon(R.drawable.football_icon)));
        sportList.add(new Sport("PingPong", true, new Sport.SportIcon(R.drawable.pingpong_icon)));
        sportList.add(new Sport("BasketBall", false, new Sport.SportIcon(R.drawable.basketball_icon)));
        sportList.add(new Sport("VolleyBall", false, new Sport.SportIcon(R.drawable.volleyball_icon)));
        sportList.add(new Sport("Tennis", true, new Sport.SportIcon(R.drawable.tennis_icon)));
        sportList.add(new Sport("Squash", true, new Sport.SportIcon(R.drawable.squash_icon)));
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
                        .url("http://192.168.1.182:3000/getUsername?email=" + email) // Replace with your actual URL
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
            // return result;
        }

    }

    private class PostRequestsStatus extends AsyncTask<String, Void, Void> {

        private String jsonData;
        private String url;

        public PostRequestsStatus(String url, String jsonData) {
            this.jsonData = jsonData;
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection;
            try {
                URL url = new URL(this.url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(jsonData);
                writer.flush();
                writer.close();
                out.close();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                }

                connection.disconnect();
                return null;
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}