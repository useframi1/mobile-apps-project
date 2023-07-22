package edu.aucegypt.gymwya;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {
    String urlSport;
    String jsonString2;
    Uri selectedImage;
    ArrayList<Sport.SportIcon> sport_icons = new ArrayList<>();
    Button cancel,save, editProfilePic;
    EditText name,username,bio;

    ImageView back, profilePic;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    private static final int REQUEST_PICK_IMAGE = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        db.collection("Images")
                .document(dataModel.currentUser.email)
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

        cancel= findViewById(R.id.cancelButton);
        save= findViewById(R.id.saveButton);
        back = findViewById(R.id.back);
        name = findViewById(R.id.edit_name);
        username = findViewById(R.id.edit_username);
        bio = findViewById(R.id.edit_bio);
        editProfilePic = findViewById(R.id.change_photo);
        profilePic = findViewById(R.id.profile_pic);

        sport_icons.add(new Sport.SportIcon(R.drawable.football_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.volleyball_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.tennis_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.squash_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.basketball_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.swimming_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.pingpong_icon));
        sport_icons.add(new Sport.SportIcon(R.drawable.gym_icon));


        RecyclerView iconRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        iconRecyclerView.setLayoutManager(layoutManager);

        IconsAdapter iconAdapter = new IconsAdapter(sport_icons, false, true);
        iconRecyclerView.setAdapter(iconAdapter);

        cancel.setOnClickListener(view -> finish());
        editProfilePic.setOnClickListener(vv -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editName = name.getText().toString();
                String editUserName = username.getText().toString();;
                String editBio = bio.getText().toString();

                try {
                    uploadImage(selectedImage);
                    // Create a JSON object with the user-entered data
                    JSONObject postData = new JSONObject();
                    JSONObject preferredSportsData = new JSONObject();


                    postData.put("username", dataModel.currentUser.username);
                    postData.put("newUsername", editUserName);
                    postData.put("name", editName);
                    postData.put("bio", editBio);

                    String jsonString = postData.toString();

                    ArrayList<Integer> sportsIconsSelected = (ArrayList<Integer>) iconAdapter.getPressedIconIds();

                    ArrayList<String> sportsSelectedNames = (ArrayList<String>) iconAdapter.getSelectedSportNames(sportsIconsSelected);

                    JSONArray sportsArrayJSON = new JSONArray(sportsSelectedNames);

                    preferredSportsData.put("username", editUserName);
                    preferredSportsData.put("preferredSports", sportsArrayJSON);


                    jsonString2 = preferredSportsData.toString();

                    String url = "http://192.168.56.1:3000/updateUser";
                    urlSport = "http://192.168.56.1:3000/addPreferredSports";


                    PostEditUser asyncTask = new PostEditUser(url, jsonString);
                    asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });
        back.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            // set image in imageview pfp
            profilePic.setImageURI(selectedImage);
            // testing toast
            Toast.makeText(getApplicationContext(), "Profile Picture Set ", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImage(Uri imageUri) throws IOException {
        if (imageUri != null) {
            // Create a unique filename for the image
            String filename = UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child(filename);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        Task<Uri> downloadUrlTask = imageRef.getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Do something with the image URL, e.g., save it to a user profile
                            // Add your desired code here to handle the image URL

                            // Example: Add the image URL to Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("image", imageUrl);

// Check if the document with the same email exists
                            db.collection("Images")
                                    .document(dataModel.currentUser.email)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Document exists, update it
                                            if (task.getResult().exists()) {
                                                db.collection("Images")
                                                        .document(dataModel.currentUser.email)
                                                        .update(user)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(getApplicationContext(), "Image updated successfully",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(EditProfile.this, Profile.class);
                                                            startActivity(i);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(getApplicationContext(), "Failed to update image",
                                                                    Toast.LENGTH_SHORT).show();
                                                            // Handle failure, if needed
                                                        });
                                            } else { // Document doesn't exist, create it
                                                db.collection("Images")
                                                        .document(dataModel.currentUser.email)
                                                        .set(user)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(getApplicationContext(), "Image uploaded successfully",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(EditProfile.this, Profile.class);
                                                            startActivity(i);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(getApplicationContext(), "Failed to upload image",
                                                                    Toast.LENGTH_SHORT).show();
                                                            // Handle failure, if needed
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error checking document", Toast.LENGTH_SHORT).show();
                                            // Handle failure, if needed
                                        }
                                    });

                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private class PostEditUser extends AsyncTask<String, Void, Void> {

        private String jsonData;
        private String url;
        public PostEditUser(String url,String jsonData) {
            this.jsonData = jsonData;
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... params) {
            // String urlString = params[0];
            String result = "";
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
                    result = response.toString();
                } else {
                    result = "Error: " + responseCode;
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
        @Override
        protected void onPostExecute(Void result) {
            PostEditUser asyncTask2 = new PostEditUser(urlSport, jsonString2);
            asyncTask2.execute();

        }

    }
}