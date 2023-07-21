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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        getGroupMembersTask.execute("http://192.168.1.182:3000/");


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
            finish();
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
            Intent intent = new Intent(this, HomePage.class);
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
                HttpURLConnection connection = getHttpRequest(url+"getGroupMembers/?ID=" + group.ID);
                if (connection != null) {
                    String response = getResponse(connection);
                    JSONArray json = new JSONArray(response);
//                    if (Objects.equals(group.creator.username, dataModel.currentUser.username))
//                        group.members.add(dataModel.currentUser);
                    for (int i = 0; i < json.length(); i++) {
                        String username = json.getJSONObject(i).getString("username");
                        int j = 0;
                        while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, username)) {
                            j++;
                        }
                        if (j<dataModel.users.size())
                            group.members.add(dataModel.users.get(j));
                        if (username.equals(dataModel.currentUser.username)) {
                            group.members.add(0,dataModel.currentUser);
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
            capacity.setText(group.members.size()+"/10");
            adapter = new ViewGroupAdapter(ViewGroup.this, group.members, group);
            listView = findViewById(R.id.members_list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (!dataModel.currentUser.username.equals(group.members.get(position).username)){
                    Intent intent = new Intent(ViewGroup.this, VisitProfile.class);
                    intent.putExtra("User", group.members.get(position));
                    startActivity(intent);
                }
            });
        }
    }
}

class ViewGroupAdapter extends ArrayAdapter<User> {
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    ArrayList<User> members;
    GroupMeeting group;
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

        if (Objects.equals(member.username, dataModel.currentUser.username)) {
            kick.setVisibility(View.GONE);
            arrow_btn.setVisibility(View.INVISIBLE);
        }

        if (!group.creator.username.equals(dataModel.currentUser.username))
            kick.setVisibility(View.GONE);


        memberName.setText(member.username.equals(dataModel.currentUser.username)?"You":member.name);
//        memberPic.setImageResource(member.imageId);


        return convertView;
    }
}

