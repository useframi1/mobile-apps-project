package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.grpc.internal.JsonParser;

public class API extends AsyncTask<String, Void, String> {
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    SharedPreferences credentials;
    boolean isSignIn;

    OnStart onStart;

    public API(OnStart onStart) {
        this.isSignIn = false;
        this.onStart = onStart;
    }
    public API(boolean isSignIn, SharedPreferences credentials, OnStart onStart) {
        this.isSignIn = isSignIn;
        this.credentials = credentials;
        this.onStart = onStart;
    }
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
        HttpURLConnection connection_getUsername;
        try {
            if (isSignIn) {
                connection_getUsername = getHttpRequest(url+"getUsername/?email="+dataModel.currentUser.email);
                if (connection_getUsername != null) {
                    String response = getResponse(connection_getUsername);
                    JSONArray json = new JSONArray(response);
                    dataModel.currentUser.username = json.getJSONObject(0).getString("username");
                    SharedPreferences.Editor editor = credentials.edit();
                    editor.putString("username", dataModel.currentUser.username);
                    editor.commit();
                }
            }

            HttpURLConnection connection_getAllUsers = getHttpRequest(url+"getAllUsers/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getRequests = getHttpRequest(url+"getRequests/?creator="+dataModel.currentUser.username);
            HttpURLConnection connection_getUser = getHttpRequest(url+"getUser/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getMeetings = getHttpRequest(url+"getMeetings/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getGroups = getHttpRequest(url+"getGroups/?creator="+dataModel.currentUser.username);
            HttpURLConnection connection_createdMeetings = getHttpRequest(url+"getCreatedMeetings/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_createdGroups = getHttpRequest(url+"getCreatedGroups/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_acceptedMeetings = getHttpRequest(url+"getAcceptedMeetings/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getPreferredSports = getHttpRequest(url+"getPreferredSports/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getJoinedGroups = getHttpRequest(url+"getJoinedGroups/?username="+dataModel.currentUser.username);


            if(connection_getUser != null) {
                String response = getResponse(connection_getUser);
                JSONArray json = new JSONArray(response);
                dataModel.currentUser.name = json.getJSONObject(0).getString("name");
                dataModel.currentUser.email = json.getJSONObject(0).getString("email");
                dataModel.currentUser.age = json.getJSONObject(0).getInt("age");
                dataModel.currentUser.bio = json.getJSONObject(0).getString("bio");

            } if(connection_getAllUsers != null) {
                String response = getResponse(connection_getAllUsers);
                JSONArray json = new JSONArray(response);
                for (int i = 0; i < json.length(); i++) {
                    dataModel.users.add(new User(json.getJSONObject(i).getString("name"), json.getJSONObject(i).getString("email"), json.getJSONObject(i).getString("username"), json.getJSONObject(i).getInt("age"),json.getJSONObject(i).getString("bio")));
                }
            } if(connection_getMeetings != null) {
                String response = getResponse(connection_getMeetings);
                JSONArray json = new JSONArray(response);
                int j = 0;
                for (int i = 0; i < json.length(); i++) {
                    while (j<dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("creator"))) {
                        j++;
                    }
                    dataModel.individualMeetings.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"), dataModel.users.get(j),  null));
                    j = 0;
                }
            } if (connection_getRequests != null) {
                String response = getResponse(connection_getRequests);
                JSONArray json = new JSONArray(response);
                int j = 0;
                for (int i = 0; i < json.length(); i++) {
                    while(j<dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("username"))) {
                        j++;
                    }
                    dataModel.currentUser.requests.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"), dataModel.currentUser, dataModel.users.get(j)));
                    if (json.getJSONObject(i).getInt("seen") == 0)
                        dataModel.currentUser.unseenRequests++;
                    j = 0;
                }
            } if(connection_getGroups != null) {
                String response = getResponse(connection_getGroups);
                JSONArray json = new JSONArray(response);
                int j = 0;
                for (int i = 0; i < json.length(); i++) {
                    while(j<dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("creator"))) {
                        j++;
                    }
                    dataModel.groupMeetings.add(new GroupMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("gDate"), dataModel.users.get(j), new ArrayList<>(), json.getJSONObject(i).getString("name")));
                    j = 0;
                }

                for (int i = 0; i < dataModel.groupMeetings.size(); i++) {
                    System.out.println(dataModel.groupMeetings.get(i).ID);
                }

            } if(connection_createdMeetings != null) {
                String response = getResponse(connection_createdMeetings);
                JSONArray json = new JSONArray(response);
                int j = 0;
                for (int i = 0; i < json.length(); i++) {
                    while(j<dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("partner"))) {
                        j++;
                    }
                    dataModel.currentUser.createdMeetings.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"), dataModel.currentUser, j < dataModel.users.size() ? dataModel.users.get(j) : null));
                    j = 0;
                }

            } if(connection_createdGroups != null) {
                String response = getResponse(connection_createdGroups);
                JSONArray json = new JSONArray(response);
                for (int i = 0; i < json.length(); i++) {
                    dataModel.currentUser.createdGroups.add(new GroupMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("gDate"), dataModel.currentUser, new ArrayList<>(),json.getJSONObject(i).getString("name")));
                }

            } if(connection_acceptedMeetings != null) {
                String response = getResponse(connection_acceptedMeetings);
                JSONArray json = new JSONArray(response);
                int j = 0;
                for (int i = 0; i < json.length(); i++) {
                    while(j<dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("creator"))) {
                        j++;
                    }
                    dataModel.currentUser.currentMatches.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"), dataModel.users.get(j), json.getJSONObject(i).isNull("partner")?null: dataModel.currentUser));
                    j = 0;
                }
            } if (connection_getPreferredSports != null) {
                String response = getResponse(connection_getPreferredSports);
                JSONArray json = new JSONArray(response);
                for (int i = 0; i < json.length(); i++) {
                    dataModel.currentUser.preferredSports.add(json.getJSONObject(i).getString("sport"));
                }
            } if (connection_getJoinedGroups != null) {
                String response = getResponse(connection_getJoinedGroups);
                System.out.println(response);
                JSONArray json = new JSONArray(response);
                for (int i = 0; i < json.length(); i++) {
                    int j = 0;
                    while (j<dataModel.groupMeetings.size() && dataModel.groupMeetings.get(j).ID != json.getJSONObject(i).getInt("ID"))
                        j++;
                    if (j!=dataModel.groupMeetings.size()) {
                        dataModel.currentUser.joinedGroups.add(dataModel.groupMeetings.get(j));
                        dataModel.groupMeetings.get(j).currentUserJoined = true;
                    }
                }
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return "Success";
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);

        onStart.onTaskComplete();
    }

    public interface OnStart {
        void onTaskComplete();
    }
}
