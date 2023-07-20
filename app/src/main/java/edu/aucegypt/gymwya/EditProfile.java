package edu.aucegypt.gymwya;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class EditProfile extends AppCompatActivity {
    String urlSport;
    String jsonString2;

    ArrayList<Sport.SportIcon> sport_icons = new ArrayList<>();
    Button cancel,save;
    EditText name,username,bio;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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

        cancel= findViewById(R.id.cancelButton);
        save= findViewById(R.id.saveButton);
        back = findViewById(R.id.back);
        name = findViewById(R.id.edit_name);
        username = findViewById(R.id.edit_username);
        bio = findViewById(R.id.edit_bio);

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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editName = name.getText().toString();
                String editUserName = username.getText().toString();;
                String editBio = bio.getText().toString();

                try {
                    // Create a JSON object with the user-entered data
                    JSONObject postData = new JSONObject();
                    JSONObject preferredSportsData = new JSONObject();


                    postData.put("username", "MGhobary256");
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
                }

                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(v -> {
            finish();
        });
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