package edu.aucegypt.gymwya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    Button editProfile;
    RecyclerView recyclerView;
    ArrayList<Sport.SportIcon> iconsList = new ArrayList<>();
    IconsAdapter mAdapter;

    ImageView profile_pic;
    String email;
    TextView currentMatches, createdMeetings;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_pic = findViewById(R.id.profile_pic);
        email = getIntent().getStringExtra("email");
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
                                    .into(profile_pic);
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

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.home) {
                i = new Intent(this, HomePage.class);
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, Chats.class);
            } else {
                return false;
            }
            startActivity(i);
            return true;
        });

        recyclerView = findViewById(R.id.recyclerView);
        iconsList.add(new Sport.SportIcon(R.drawable.football_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.volleyball_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.tennis_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.squash_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.basketball_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.swimming_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.pingpong_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.gym_icon));

        mAdapter = new IconsAdapter(iconsList, true, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        editProfile = findViewById(R.id.edit_profile);
        currentMatches = findViewById(R.id.view_current_matches);
        createdMeetings = findViewById(R.id.view_created_matches);

        editProfile.setOnClickListener(view -> {
            Intent intent= new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
        });

        currentMatches.setOnClickListener(view -> {
            Intent intent= new Intent(Profile.this, PeopleMatched.class);
            startActivity(intent);
        });

        createdMeetings.setOnClickListener(view-> {
            Intent intent = new Intent (Profile.this, ViewCreatedMeetings.class);
            startActivity(intent);
        });
    }
}