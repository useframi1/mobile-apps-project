package edu.aucegypt.gymwya;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
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

    private void postHttpRequest(String url, String jsonString) throws IOException, JSONException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();

        // Set request method
        connection.setRequestMethod("POST");

        // Enable input and output streams
        connection.setDoInput(true);
        connection.setDoOutput(true);

        // Set request headers
        connection.setRequestProperty("Content-Type", "application/json");

        // Create JSON data
//        String jsonData = "{\"username\": \"joe\", \"email\":\"yousseframi@aucegypt.edu\", \"name\":\"youssef\", \"age\":20, \"bio\":\"hellooo\"}"; // Replace with your JSON data



        // Write JSON data to the request body
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(jsonString);
        outputStream.flush();
        outputStream.close();

        // Get response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
//                    JSONObject jsonObject = new JSONObject();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                try {
//                    jsonObject.append("username", "ahhhhhhh");
//                    jsonObject.append("email" ,"yousseframi@aucegypt.edu");
//                    jsonObject.append("name", "youssef");
//                    jsonObject.append("age", 20);
//                    jsonObject.append("bio", "heloooo");
//                    String jsonData = jsonObject.toString();
//                    postHttpRequest(url+"createUser", jsonData);
//                } catch (JSONException | IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
        try {
            HttpURLConnection connection_getAllUsers = getHttpRequest(url+"getAllUsers");
            HttpURLConnection connection_getRequests = getHttpRequest(url+"getRequests/?creator="+dataModel.currentUser.username);
            HttpURLConnection connection_getUser = getHttpRequest(url+"getUser/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getMeetings = getHttpRequest(url+"getMeetings/?username="+dataModel.currentUser.username);
            HttpURLConnection connection_getGroups = getHttpRequest(url+"getGroups/?creator="+dataModel.currentUser.username);
            HttpURLConnection connection_createdMeetings = getHttpRequest(url+"getCreatedMeetings/?user="+dataModel.currentUser.username);
            HttpURLConnection connection_createdGroups = getHttpRequest(url+"getCreatedGroups/?user="+dataModel.currentUser.username);
            HttpURLConnection connection_acceptedMeetings = getHttpRequest(url+"getAcceptedMeetings/?user="+dataModel.currentUser.username);


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
                int j = 0, k = 0;
                for (int i = 0; i < json.length(); i++) {
                    while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("creator"))) {
                        j++;
                    }

                    dataModel.currentUser.requests.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"), dataModel.currentUser, dataModel.users.get(j)));
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
                }
            } if(connection_getGroups != null) {
                String response = getResponse(connection_getGroups);
                JSONArray json = new JSONArray(response);
                int j = 0;
                for (int i = 0; i < json.length(); i++) {
                    while(j<dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username, json.getJSONObject(i).getString("creator"))) {
                        j++;
                    }
//                    HttpURLConnection connection_getGroupMembers = getHttpRequest(url+"getGroupMembers/?ID="+json.getJSONObject(i).getInt("ID"));
//                    if (connection_getGroupMembers!=null) {
//                        String temp = getResponse(connection_getUser);
//                        JSONArray tempJson = new JSONArray(response);
//                        ArrayList<User> members = new ArrayList<>();
//                        for (int k = 0; k < tempJson.length(); k++) {
//                            members
//                        }
//                    }

//                    dataModel.groupMeetings.add(new GroupMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("gDate"), dataModel.users.get(j), dataModel))
                }

            } if(connection_createdMeetings != null) {
                String response = getResponse(connection_createdMeetings);
                JSONArray json = new JSONArray(response);
                for (int i = 0; i < json.length(); i++) {
                    dataModel.currentUser.createdMeetings.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"), json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"), json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"), dataModel.currentUser, dataModel.users.get(i)));
                }

            } if(connection_createdGroups != null) {
                String response = getResponse(connection_createdGroups);
            } if(connection_acceptedMeetings != null) {
                String response = getResponse(connection_acceptedMeetings);
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        return "Success";
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
    }
}
