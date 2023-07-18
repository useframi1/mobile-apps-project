package edu.aucegypt.gymwya;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.common.net.MediaType;
import com.google.firebase.FirebaseApp;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;

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
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        create = findViewById(R.id.createUserButton);
        back = findViewById(R.id.back);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePicture);
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
            if (Bio.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "Please enter a bio", Toast.LENGTH_SHORT).show();
            }
            else {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }



            //call the async task


                    try {
                        // Create a JSON object with the user-entered data
                        JSONObject postData = new JSONObject();
                        postData.put("name", Name);
                        postData.put("username", userName);
                        postData.put("email", "n.kasaby@aucegypt.edu");
                        postData.put("age", Age);
                        postData.put("bio", Bio);

                        // Convert the JSONObject to a string
                        String jsonString = postData.toString();

                        // Replace the URL with your actual Node.js API endpoint
                        String url = "http://192.168.56.1:3000/createUser";

                        // Execute the POST request asynchronously using the PostCreateGroup AsyncTask
                        PostCreateUser asyncTask = new PostCreateUser(url, jsonString);
                        asyncTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                //  new CreateUserTask().execute(userName, Name, Bio, Age, email);

        });
            try {
                uploadImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Perform the document retrieval and user creation logic

            //put the data in the database


        back.setOnClickListener(vv -> {
            finish();
        });

    }

    private class PostCreateUser extends AsyncTask<String, Void, Void> {

        private String jsonData;
        private String url;
        public PostCreateUser(String url,String jsonData) {
            this.jsonData = jsonData;
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... params) {
            // String urlString = params[0];
            String result = "";
            HttpURLConnection connection;
            try {
                // String urlStr = "http://192.168.56.1/createUser";
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
    }


    //    private class CreateUserTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String userName = params[0];
//            String Name = params[1];
//            String Bio = params[2];
//            String Age = params[3];
//            String email = params[4];
//
//            // Prepare the JSON request body
//            JSONObject jsonBody = new JSONObject();
//            try {
//                jsonBody.put("username", userName);
//                jsonBody.put("name", Name);
//                jsonBody.put("bio", Bio);
//                jsonBody.put("age", Age);
//                jsonBody.put("email", "email");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            String requestBody = jsonBody.toString();
//
//            // Create an HTTP request
//            okhttp3.Request request = new Request.Builder()
//                    .url("http://localhost:3000/createUser")
//                    .post(RequestBody.create(JSON, requestBody))
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    return response.body().string();
//                } else {
//                    return null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null && result.equals("1")) {
//                // User created successfully
//                Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(CreateUser.this, HomePage.class);
//                startActivity(intent);
//            } else {
//                // Failed to create user
//                Toast.makeText(getApplicationContext(), "Failed to create user", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
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