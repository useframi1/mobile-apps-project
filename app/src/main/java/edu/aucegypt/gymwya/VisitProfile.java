package edu.aucegypt.gymwya;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VisitProfile extends AppCompatActivity {
    Button message;
    ImageView back, pic;
    TextView name, username, age, bio;
    User user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_profile);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("User");

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

        back = findViewById(R.id.back);
        pic = findViewById(R.id.profile_pic);
        message = findViewById(R.id.message);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        age = findViewById(R.id.age);
        bio = findViewById(R.id.bio);

        name.setText(user.name);
        username.setText(user.username);
        age.setText(String.valueOf(user.age));
        bio.setText(user.bio);
        setImage();

        VisitProfileTask visitProfileTask = new VisitProfileTask();
        visitProfileTask.execute("http://192.168.1.182:3000/");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // message.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View view) {
        // Intent i = new Intent(VisitProfile.this, Chat.class);
        // startActivity(i);
        // }
        // });
    }

    public void setImage() {
        // Retrieve the image URL from Firestore based on the user's email
        db.collection("Images")
                .document(user.email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null) {
                            // Image URL retrieved successfully, load the image into ImageView
                            // You can use any image loading library or method here, for example, Glide or
                            // Picasso
                            // Here's an example using Glide:
                            Glide.with(this)
                                    .load(imageUrl)
                                    .apply(new RequestOptions()) // Optional: Add a placeholder image
                                    .into(pic);
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
    }

    public class VisitProfileTask extends AsyncTask<String, Void, String> {

        private String getResponse(HttpURLConnection connection) throws IOException {
            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return the response as a string
            return response.toString();
        }

        private HttpURLConnection getHttpRequest(String url) throws IOException {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
                return connection;

            return null;
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];

            try {
                HttpURLConnection connection = getHttpRequest(url + "getPreferredSports/?username=" + user.username);
                if (connection != null) {
                    String response = getResponse(connection);
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        user.preferredSports.add(json.getJSONObject(i).getString("sport"));
                    }
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            RecyclerView iconRecyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(VisitProfile.this,
                    LinearLayoutManager.HORIZONTAL, false);
            iconRecyclerView.setLayoutManager(layoutManager);

            // Create and set the adapter
            IconsAdapter iconAdapter = new IconsAdapter(user.getPreferredSportsIcons(), false, false);
            iconRecyclerView.setAdapter(iconAdapter);
        }
    }
}