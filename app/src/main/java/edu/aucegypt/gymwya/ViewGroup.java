package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class ViewGroup extends AppCompatActivity implements View.OnClickListener {
    ViewGroupAdapter adapter;
    ArrayList<User> members = new ArrayList<>();
    TextView groupName;
    ImageView back;
    ListView listView;
    Button joinGroup;

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
                i = new Intent(this, CreateGroup.class);
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
//            members = (ArrayList<User>) bundle.getSerializable("Members");
//        }
        members.add(new User("Nour", "email", "bio", 19, "password"));

        adapter = new ViewGroupAdapter(this, members);

        listView = findViewById(R.id.members_list);
        groupName = findViewById(R.id.group_name);
        back = findViewById(R.id.back);
        joinGroup = findViewById(R.id.join_group);

        groupName.setText("barbie");

        back.setOnClickListener(this);
        joinGroup.setOnClickListener(this);

        listView.setAdapter(adapter);
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

            JSONObject members = new JSONObject();

            ArrayList<String> groupMembers = new ArrayList<>();
            groupMembers.add("feweeee");
            JSONArray groupMembersJSON = new JSONArray(groupMembers);

            try {

                members.put("ID", 38);
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
                    System.out.println("heere");
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
//            Intent intent = new Intent(ViewGroup.this, HomePage.class);
//            startActivity(intent);
        }
    }

}

class ViewGroupAdapter extends ArrayAdapter<User> {
    Button viewProfile;
    public ViewGroupAdapter(Context context, ArrayList<User> members) {
        super(context, 0, members);
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
        viewProfile = convertView.findViewById(R.id.view_profile);

        memberName.setText(member.name);
//        memberPic.setImageResource(member.imageId);

        viewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VisitProfile.class);
            intent.putExtra("User", member);
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
