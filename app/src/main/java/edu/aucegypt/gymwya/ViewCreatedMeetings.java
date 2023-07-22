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

public class ViewCreatedMeetings extends AppCompatActivity {

    ViewMeetingsCreatedAdapter adapter;
    ImageView back;
    ListView listView;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    ArrayList<IndividualMeeting> meetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_created_meetings);

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

        meetings = new ArrayList<>();
        meetings.addAll(dataModel.currentUser.createdMeetings);
        meetings.addAll(dataModel.currentUser.currentMatches);
        adapter = new ViewMeetingsCreatedAdapter(this, meetings);

        listView = findViewById(R.id.view_created_meetings_list);
        back = findViewById(R.id.back);

        // groupName.setText(name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            if (!meetings.get(position).creator.username.equals(dataModel.currentUser.username)) {
                Intent intent = new Intent(this, VisitProfile.class);
                intent.putExtra("User", meetings.get(position).creator);
                startActivity(intent);
            }
        });
    }

    class ViewMeetingsCreatedAdapter extends ArrayAdapter<IndividualMeeting> {

        public ViewMeetingsCreatedAdapter(Context context, ArrayList<IndividualMeeting> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IndividualMeeting meeting = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.created_meetings_list, parent, false);
            }

            TextView sport = convertView.findViewById(R.id.sport);
            TextView from = convertView.findViewById(R.id.from);
            TextView to = convertView.findViewById(R.id.to);
            TextView date = convertView.findViewById(R.id.date);
            TextView status = convertView.findViewById(R.id.status);
            TextView creator = convertView.findViewById(R.id.creator);
            ImageView arrow_btn = convertView.findViewById(R.id.arrow_button);
            Button cancel = convertView.findViewById(R.id.cancel);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CancelTask cancelTask;
                    JSONObject jsonData = new JSONObject();
                    if (meeting.creator.username.equals(dataModel.currentUser.username)) {
                        try {
                            jsonData.put("ID", meeting.ID);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        cancelTask = new CancelTask(meeting, jsonData, "cancelMeeting");
                    } else {
                        try {
                            jsonData.put("ID", meeting.ID);
                            jsonData.put("username", dataModel.currentUser.username);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        cancelTask = new CancelTask(meeting, jsonData, "cancelRequest");
                    }
                    cancelTask.execute("http://192.168.1.182:3000/");
                }
            });

            if (meeting.creator.username.equals(dataModel.currentUser.username))
                arrow_btn.setVisibility(View.INVISIBLE);
            else
                arrow_btn.setVisibility(View.VISIBLE);
            creator.setText(Objects.equals(meeting.creator.username, dataModel.currentUser.username) ? "You"
                    : meeting.creator.username);
            String firstChar = meeting.sport.substring(0, 1).toUpperCase();
            String restOfString = meeting.sport.substring(1).toLowerCase();
            sport.setText(firstChar + restOfString);
            from.setText(meeting.start);
            to.setText(meeting.end);
            date.setText(meeting.date.substring(0, 10));
            if (meeting.partner == null) {
                status.setText("Pending");
                status.setTextColor(getResources().getColor(R.color.dark_grey));
            } else {
                status.setText("Matched");
                status.setTextColor(getResources().getColor(R.color.blue));
            }

            return convertView;
        }
    }

    class CancelTask extends AsyncTask<String, Void, String> {
        IndividualMeeting meeting;
        JSONObject jsonData;
        String api;

        public CancelTask(IndividualMeeting meeting, JSONObject jsonData, String api) {
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
            if (Objects.equals(api, "cancelMeeting")) {
                while (i < dataModel.currentUser.createdMeetings.size()
                        && meeting.ID != dataModel.currentUser.createdMeetings.get(i).ID) {
                    i++;
                }
                dataModel.currentUser.createdMeetings.remove(i);
            } else if (Objects.equals(api, "cancelRequest")) {
                while (i < dataModel.currentUser.currentMatches.size()
                        && meeting.ID != dataModel.currentUser.currentMatches.get(i).ID) {
                    i++;
                }
                dataModel.currentUser.currentMatches.remove(i);
            }
            meetings.remove(meeting);
            adapter = new ViewMeetingsCreatedAdapter(ViewCreatedMeetings.this, meetings);
            listView.setAdapter(adapter);
        }
    }
}