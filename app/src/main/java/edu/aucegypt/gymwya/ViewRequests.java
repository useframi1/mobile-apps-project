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

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class ViewRequests extends AppCompatActivity {

    ViewRequestAdapter adapter;
    ArrayList<User> match_list = new ArrayList<>();


    // TextView groupName;
    ImageView back;
    ListView listView;

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

//        Bundle bundle = getIntent().getExtras();
//        String name = "Group Name";
//        if (bundle != null) {
//            name = bundle.getString("Team");
//            match_list = (ArrayList<User>) bundle.getSerializable("Members");
//        }

        //String name, String email, String username, int age, String bio
        match_list.add(new User("youssef", R.drawable.ghaleb));
        match_list.add(new User("nour", R.drawable.nour));


        adapter = new ViewRequestAdapter(this, match_list);

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
            intent.putExtra("User", match_list.get(position).name);
            startActivity(intent);
        });
    }
    class ViewRequestAdapter extends ArrayAdapter<User> {
        Button decline, confirm;

        public ViewRequestAdapter(Context context, ArrayList<User> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User person = getItem(position);

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

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        JSONObject postData = new JSONObject();
                        postData.put("ID", 5);
                        postData.put("partner", "Elbarbary");

                        String jsonString = postData.toString();

                        String url = "http://192.168.56.1:3000/updatePartner";

                        PostRequests asyncTask = new PostRequests(url, jsonString);
                        asyncTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    match_list.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        JSONObject postData = new JSONObject();
                        postData.put("ID", 15);
                        postData.put("partner", "MGhobary256");

                        String jsonString = postData.toString();

                        String url = "http://192.168.56.1:3000/updatePartner";

                        PostRequests asyncTask = new PostRequests(url, jsonString);
                        asyncTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            return convertView;
        }
    }

    private class PostRequests extends AsyncTask<String, Void, Void> {

        private String jsonData;
        private String url;
        public PostRequests(String url, String jsonData) {
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
