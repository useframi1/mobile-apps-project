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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class ViewCreatedGroups extends AppCompatActivity {

    ViewGroupsCreatedAdapter adapter;
    ImageView back;
    ListView listView;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    ArrayList<GroupMeeting> allGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_created_groups);


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

        allGroups = new ArrayList<>();
        allGroups.addAll(dataModel.currentUser.createdGroups);
        allGroups.addAll(dataModel.currentUser.joinedGroups);

        adapter = new ViewGroupsCreatedAdapter(this, allGroups);

        listView = findViewById(R.id.view_created_groups_list);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(this, edu.aucegypt.gymwya.ViewGroup.class);
            intent.putExtra("Group", allGroups.get(position));
            startActivity(intent);
        });
    }


    class ViewGroupsCreatedAdapter extends ArrayAdapter<GroupMeeting> {

        public ViewGroupsCreatedAdapter(Context context, ArrayList<GroupMeeting> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupMeeting group = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.created_groups_list, parent, false);
            }

            TextView groupName = convertView.findViewById(R.id.group_name);
            TextView sport = convertView.findViewById(R.id.sport);
            TextView from = convertView.findViewById(R.id.from);
            TextView to = convertView.findViewById(R.id.to);
            TextView date = convertView.findViewById(R.id.date);
            Button delete_btn = convertView.findViewById(R.id.delete);

            if (!Objects.equals(group.creator.username, dataModel.currentUser.username)) {
                delete_btn.setText("Leave");
            }

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LeaveTask leaveTask;
                    JSONObject jsonData = new JSONObject();
                    if (group.creator.username.equals(dataModel.currentUser.username)) {
                        try {
                            jsonData.put("ID", group.ID);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        leaveTask = new LeaveTask(group, jsonData, "deleteGroup");
                    } else {
                        try {
                            jsonData.put("ID", group.ID);
                            jsonData.put("username", dataModel.currentUser.username);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        leaveTask = new LeaveTask(group, jsonData, "leaveGroup");
                    }
                    leaveTask.execute("http://192.168.1.182:3000/");
                }
            });

            groupName.setText(group.name);
            String firstChar = group.sport.substring(0, 1).toUpperCase();
            String restOfString = group.sport.substring(1).toLowerCase();
            sport.setText(firstChar+restOfString);
            from.setText(group.start);
            to.setText(group.end);
            date.setText(group.date.substring(0,10));

            return convertView;
        }
    }

    class LeaveTask extends AsyncTask<String, Void, String> {
        GroupMeeting group;
        JSONObject jsonData;
        String api;

        public LeaveTask(GroupMeeting group, JSONObject jsonData, String api) {
            this.group = group;
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
            try {
                postHttpRequest(url+api);

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            int i = 0;
            if (Objects.equals(api, "deleteGroup")) {
                while (i < dataModel.currentUser.createdGroups.size() && group.ID != dataModel.currentUser.createdGroups.get(i).ID){
                    i++;
                }
                dataModel.currentUser.createdGroups.remove(i);
            }
            else if (Objects.equals(api, "leaveGroup")) {
                while (i < dataModel.currentUser.joinedGroups.size() && group.ID != dataModel.currentUser.joinedGroups.get(i).ID){
                    i++;
                }
                dataModel.currentUser.joinedGroups.remove(i);
            }
            allGroups.remove(group);
            adapter = new ViewGroupsCreatedAdapter(ViewCreatedGroups.this, allGroups);
            listView.setAdapter(adapter);
        }
    }
}
