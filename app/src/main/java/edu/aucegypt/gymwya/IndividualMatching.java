package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import java.util.Objects;

public class IndividualMatching extends AppCompatActivity {
    int i = 0;
    TextView name, bio;
    Button check, addMeeting, reject, viewProfile;
    ImageView backArrow, profilePic;
    private DataManager dataManager;
    private Data dataModel;
    String selectedSport;
    ArrayList<IndividualMeeting> meetings = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_matching);

        dataManager = DataManager.getInstance();
        dataModel = dataManager.getDataModel();
        dataModel.previousIsHome = false;

        Bundle bundle = getIntent().getExtras();
        selectedSport = bundle.getString("selectedSport");

        for (int j = 0; j < dataModel.individualMeetings.size(); j++) {
            if (Objects.equals(dataModel.individualMeetings.get(j).sport.toLowerCase(), selectedSport.toLowerCase()))
                meetings.add(dataModel.individualMeetings.get(j));
        }

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

        check = (Button) findViewById(R.id.check_button);
        backArrow = (ImageView) findViewById(R.id.back_arrow);
        profilePic = (ImageView) findViewById(R.id.profile_picture);
        addMeeting = (Button) findViewById(R.id.plus_button);
        reject = (Button) findViewById(R.id.x_button);
        viewProfile = (Button) findViewById(R.id.view_profile_button);
        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);

        if (meetings.size() == 0) {
            check.setVisibility(View.GONE);
            profilePic.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            bio.setVisibility(View.GONE);
        } else {
            check.setVisibility(View.VISIBLE);
            profilePic.setVisibility(View.VISIBLE);
            reject.setVisibility(View.VISIBLE);
            viewProfile.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            bio.setVisibility(View.VISIBLE);
            bio.setText("Wants a partner to join them at the " + meetings.get(i).sport + " from "
                    + meetings.get(i).start + " to " + meetings.get(i).end + " PM");
            // profilePic.setImageResource(users.get(i).imageId);
            setImage();

            name.setText(meetings.get(i).creator.name);

            check.setOnClickListener(view -> match_dialog());
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i++;
                    if (i == meetings.size())
                        i = 0;
                    // profilePic.setImageResource(users.get(i).imageId);
                    setImage();
                    IndividualMeeting nextMeeting = meetings.get(i);
                    name.setText(nextMeeting.creator.name);
                    bio.setText("Wants a partner to join them at the " + nextMeeting.sport + " from "
                            + nextMeeting.start + " to " + nextMeeting.end + " PM");
                }
            });
            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(IndividualMatching.this, VisitProfile.class);
                    intent.putExtra("User", meetings.get(i).creator);
                    startActivity(intent);
                }
            });
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IndividualMatching.this, HomePage.class);
                startActivity(i);
            }
        });

        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualMatching.this, CreateMeeting.class);
                startActivity(intent);
            }
        });
    }

    public void setImage() {
        // Retrieve the image URL from Firestore based on the user's email
        db.collection("Images")
                .document(meetings.get(i).creator.getEmail())
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
                                    .into(profilePic);
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

    private void match_dialog() {
        IndividualMeeting meeting = meetings.get(i);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);

        dialogTitle.setText("Match with " + meeting.creator.name);
        dialogMessage.setText("Are you sure you want to match with " + meeting.creator.name + "?");
        cancelButton.setText("Cancel");
        confirmButton.setText("Confirm");

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        confirmButton.setOnClickListener(view -> {
            try {
                JSONObject postData = new JSONObject();
                postData.put("ID", meeting.ID);
                postData.put("username", dataModel.currentUser.username);

                String jsonString = postData.toString();

                String url = "http://192.168.1.182:3000/addRequest";

                PostCreateRequest asyncTask = new PostCreateRequest(url, jsonString);
                asyncTask.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private class PostCreateRequest extends AsyncTask<String, Void, Void> {

        private String jsonData;
        private String url;

        public PostCreateRequest(String url, String jsonData) {
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
