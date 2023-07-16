package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CreateUser extends AppCompatActivity {
    private GridViewAdapter gridAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email;
    private static final int REQUEST_PICK_IMAGE = 1;
    private StorageReference storageReference;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    Button create, uploadProfilePictureButton;

    String userName;
    ImageView back, profilePicture;
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;

    Uri selectedImage;
    AtomicReference<Boolean> Error = new AtomicReference<>(false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        create = findViewById(R.id.createUserButton);
        back = findViewById(R.id.back);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePictureButton);
        profilePicture = findViewById(R.id.profile_picture);

          storageReference = FirebaseStorage.getInstance().getReference();


        uploadProfilePictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
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

        mAdapter = new IconsAdapter(iconsList, false, true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        create.setOnClickListener(v -> {
            // Retrieve data from the previous activity
            Intent intentt = getIntent();
            email = intentt.getStringExtra("email");
            String password = intentt.getStringExtra("password");

            // Retrieve the username from the username text field in the XML file
            userName = ((TextView) findViewById(R.id.username)).getText().toString();
            String Name = ((TextView) findViewById(R.id.name)).getText().toString();
            String Age = ((TextView) findViewById(R.id.age)).getText().toString();
            String Bio = ((TextView) findViewById(R.id.bio)).getText().toString();

            // Check if the name text field is empty
            if (Name.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if the username length is less than 6 characters
            if (userName.length() < 6) {
                Toast.makeText(getApplicationContext(), "Username must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if the age text field is a valid number between 10 and 100
            int ageValue;
                ageValue = Integer.parseInt(Age);
            if (ageValue < 10 || ageValue > 100) {
                Toast.makeText(getApplicationContext(), "Please enter a valid age between 10 and 100", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if the bio text field is empty
            if (Bio.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a bio", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                uploadImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Perform the document retrieval and user creation logic
            DocumentReference documentRef = db.collection("user").document(userName);
            documentRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Username is available, proceed with creating the user and starting the HomePage activity
                        Map<String, String> data = new HashMap<>();
                        data.put("email", email);
                        data.put("password", password);
                        data.put("age", Age); // Save the age value as well
                        // ... Add other data fields if needed
                        db.collection("user")
                                .document(userName)
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CreateUser.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateUser.this, HomePage.class);
                                    //send email to next screen
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreateUser.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                                    // Handle failure, if needed
                                });
                    }
                } else {
                    // An error occurred while checking for the document
                    // Handle the error
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            //put the data in the database
            Map<String, Object> user = new HashMap<>();
            user.put("name", Name);
            user.put("age", Age);
            user.put("bio", Bio);
            user.put("email", email);
            user.put("password", password);
            user.put("sport", mAdapter.getSelectedItems());
            db.collection("user")
                    .document(userName) // Use userName as the document ID
                    .set(user)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateUser.this, HomePage.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to create user", Toast.LENGTH_SHORT).show();
                        // Handle failure, if needed
                    });

        });


        back.setOnClickListener(v -> {
            finish();
        });

    }

    private void onComplete(Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot documentSnapshot = task.getResult();
            if (documentSnapshot.exists()) {
                Error.set(true);
                Toast.makeText(getApplicationContext(), "username exists", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "username does not exist", Toast.LENGTH_SHORT).show();
            }
        } else {
            // An error occurred while checking for the document
            // Handle the error
            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();


            //set image in imageview pfp
            profilePicture.setImageURI(selectedImage);

            //testing toast
            Toast.makeText(getApplicationContext(), "Profile Picture Set ", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImage(Uri imageUri) throws IOException {
        if (imageUri != null) {
            // Create a unique filename for the image
            String filename = "profile_picture.jpg";
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

                            db.collection("Images")
                                    .document(email) // Use userName as the document ID
                                    .set(user)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                        // Handle failure, if needed
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}