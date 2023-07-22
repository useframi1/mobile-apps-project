package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class ViewGroup extends AppCompatActivity implements View.OnClickListener {
    ViewGroupAdapter adapter;
    TextView groupName;
    ImageView back;
    ListView listView;
    Button joinGroup;
    GroupMeeting group = new GroupMeeting();


    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_group);
        FirebaseApp.initializeApp(this);
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            group = (GroupMeeting) bundle.getSerializable("Group");
        }

        GetGroupMembersTask getGroupMembersTask = new GetGroupMembersTask();
        getGroupMembersTask.execute("http://192.168.56.1:3000/");

        groupName = findViewById(R.id.group_name);
        back = findViewById(R.id.back);
        joinGroup = findViewById(R.id.join_group);

        if (Objects.equals(group.creator.username, dataModel.currentUser.username)) {
            joinGroup.setVisibility(View.GONE);
        }

        groupName.setText(group.name);

        back.setOnClickListener(this);
        joinGroup.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            Intent i = new Intent(this, GroupMatching.class);
            i.putExtra("selectedSport", group.sport);
            startActivity(i);
        } else if (v == joinGroup) {
            match_dialog();
        }
    }

    private void match_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);

        dialogTitle.setText("Join " + groupName.getText());
        dialogMessage.setText("Are you sure you want to join " + groupName.getText() + "?");
        cancelButton.setText("Cancel");
        confirmButton.setText("Confirm");

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        confirmButton.setOnClickListener(view -> {

            JSONObject members = new JSONObject();

            ArrayList<String> groupMembers = new ArrayList<>();
            groupMembers.add(dataModel.currentUser.username);
            JSONArray groupMembersJSON = new JSONArray(groupMembers);

            try {

                members.put("ID", group.ID);
                members.put("groupMembers", groupMembersJSON);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String jsonString = members.toString();

            String url = "http://192.168.56.1:3000/addGroupMembers";

            PostAddMembers postAddMembers = new PostAddMembers(url, jsonString);
            postAddMembers.execute();

            Intent intent = new Intent(ViewGroup.this, HomePage.class);
            startActivity(intent);

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    class GetGroupMembersTask extends AsyncTask<String, Void, String> {

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
                HttpURLConnection connection = getHttpRequest(url + "getGroupMembers/?ID=" + group.ID);
                if (connection != null) {
                    String response = getResponse(connection);
                    JSONArray json = new JSONArray(response);
                    // if (Objects.equals(group.creator.username, dataModel.currentUser.username))
                    // group.members.add(dataModel.currentUser);
                    for (int i = 0; i < json.length(); i++) {
                        String username = json.getJSONObject(i).getString("username");
                        int j = 0;
                        while (j < dataModel.users.size()
                                && !Objects.equals(dataModel.users.get(j).username, username)) {
                            j++;
                        }
                        if (j < dataModel.users.size())
                            group.members.add(dataModel.users.get(j));
                        if (username.equals(dataModel.currentUser.username)) {
                            group.members.add(0, dataModel.currentUser);
                            group.currentUserJoined = true;
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (group.currentUserJoined) {
                joinGroup.setVisibility(View.GONE);
            }
            TextView capacity = findViewById(R.id.capacity);
            capacity.setText(group.members.size() + "/10");
            adapter = new ViewGroupAdapter(ViewGroup.this, group.members, group);
            listView = findViewById(R.id.members_list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (!dataModel.currentUser.username.equals(group.members.get(position).username)) {
                    Intent intent = new Intent(ViewGroup.this, VisitProfile.class);
                    intent.putExtra("User", group.members.get(position));
                    startActivity(intent);
                }
            });
        }
    }

    class PostAddMembers extends AsyncTask<String, Void, String> {

        private String jsonData;
        private String url;

        public PostAddMembers(String url, String jsonData) {
            this.jsonData = jsonData;
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
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

                } else {
                    result = "Error: " + responseCode;
                }
                connection.disconnect();
                return null;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Intent intent = new Intent(ViewGroup.this, HomePage.class);
            // startActivity(intent);
        }
    }

}

class ViewGroupAdapter extends ArrayAdapter<User> {
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    ArrayList<User> members;
    GroupMeeting group;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    public ViewGroupAdapter(Context context, ArrayList<User> members, GroupMeeting group) {
        super(context, 0, members);
        this.members = members;
        this.group = group;
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        User member = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(
                    getContext()).inflate(R.layout.group_members_list, parent, false);
        }

        TextView memberName = convertView.findViewById(R.id.name);
        ImageView memberPic = convertView.findViewById(R.id.profile_picture);
        Button kick = convertView.findViewById(R.id.kick);
        ImageView arrow_btn = convertView.findViewById(R.id.arrow_button);

        db.collection("Images")
                .document(member.username)
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
                                    .into(memberPic);
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


        if (Objects.equals(member.username, dataModel.currentUser.username)) {
            kick.setVisibility(View.GONE);
            arrow_btn.setVisibility(View.INVISIBLE);
        }

        if (!group.creator.username.equals(dataModel.currentUser.username))
            kick.setVisibility(View.GONE);

        memberName.setText(member.username.equals(dataModel.currentUser.username) ? "You" : member.name);
        // memberPic.setImageResource(member.imageId);

        return convertView;
    }
}