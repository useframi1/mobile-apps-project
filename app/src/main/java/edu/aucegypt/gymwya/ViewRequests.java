package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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

public class ViewRequests extends AppCompatActivity {
    ViewRequestAdapter adapter;
    // TextView groupName;
    ImageView back;
    ListView listView;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);

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

        adapter = new ViewRequestAdapter(this, dataModel.currentUser.requests);

        listView = findViewById(R.id.request_list);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ViewRequests.this, VisitProfile.class);
            intent.putExtra("User", dataModel.currentUser.requests.get(position).partner);
            startActivity(intent);
        });
    }

    class ViewRequestAdapter extends ArrayAdapter<IndividualMeeting> {
        Button decline, confirm;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        public ViewRequestAdapter(Context context, ArrayList<IndividualMeeting> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User person = getItem(position).partner;

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.requests_list, parent, false);
            }

            TextView requestPerson = convertView.findViewById(R.id.name);
            TextView matchInfo = convertView.findViewById(R.id.info);
            ImageView personPic = convertView.findViewById(R.id.profile_picture);
            decline = convertView.findViewById(R.id.decline);
            confirm = convertView.findViewById(R.id.confirm);

            requestPerson.setText(person.name);
            matchInfo.setText(
                    getItem(position).sport + " from " + getItem(position).start + " to " + getItem(position).end);
            // personPic.setImageResource(person.imageId);

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject postData = new JSONObject();
                        postData.put("ID", dataModel.currentUser.requests.get(position).ID);
                        postData.put("username", person.username);

                        String url = "http://192.168.1.182:3000/";

                        PostRequests asyncTask = new PostRequests(getItem(position), postData, "cancelRequest");
                        asyncTask.execute(url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            // Retrieve the image URL from Firestore based on the user's email
            db.collection("Images")
                    .document(person.getEmail())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("image");
                            if (imageUrl != null) {
                                // Image URL retrieved successfully, load the image into ImageView
                                // You can use any image loading library or method here, for example, Glide or
                                // Picasso
                                // Here's an example using Glide:
                                Glide.with(this.getContext())
                                        .load(imageUrl)
                                        .apply(new RequestOptions()) // Optional: Add a placeholder image
                                        .into(personPic);
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
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        JSONObject postData = new JSONObject();
                        postData.put("ID", dataModel.currentUser.requests.get(position).ID);
                        postData.put("username", person.username);

                        String url = "http://192.168.1.182:3000/";

                        PostRequests asyncTask = new PostRequests(getItem(position), postData, "updatePartner");
                        asyncTask.execute(url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            return convertView;
        }

    }

    class PostRequests extends AsyncTask<String, Void, String> {
        IndividualMeeting meeting;
        JSONObject jsonData;
        String api;

        public PostRequests(IndividualMeeting meeting, JSONObject jsonData, String api) {
            this.meeting = meeting;
            this.jsonData = jsonData;
            this.api = api;
        }

        private HttpURLConnection postHttpRequest(String url) throws IOException, JSONException {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();

            String postData = jsonData.toString();

            os.write(postData.getBytes());
            os.flush();
            os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
                return connection;

            return null;
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            System.out.println(url);
            try {
                postHttpRequest(url + api);

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            int i = 0;
            if (Objects.equals(api, "updatePartner")){
                while (i < dataModel.individualMeetings.size() && dataModel.individualMeetings.get(i).ID != meeting.ID)
                    i++;
                if (i < dataModel.individualMeetings.size())
                    dataModel.individualMeetings.get(i).partner = meeting.partner;
            }
            i = 0;
            while (i < dataModel.currentUser.requests.size() && dataModel.currentUser.requests.get(i).ID != meeting.ID)
                i++;
            if (i < dataModel.currentUser.requests.size())
                dataModel.currentUser.requests.remove(i);
            adapter = new ViewRequestAdapter(ViewRequests.this, dataModel.currentUser.requests);
            listView.setAdapter(adapter);
        }
    }
}
