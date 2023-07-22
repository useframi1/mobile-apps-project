package edu.aucegypt.gymwya;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Objects;

public class PeriodicAsyncTask extends Service {
    private Handler handler;
    private Runnable periodicTask;
    boolean isStarting = true;
    private static final long INTERVAL_MILLIS = 10000; // 5 seconds interval

    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    SharedPreferences credentials;
    boolean isSignIn;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        startPeriodicTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            isSignIn = intent.getBooleanExtra("isSignIn", false);
        }
        // Use the Messenger to obtain the Start activity's Handler
        return START_STICKY; // Service will be restarted automatically if killed by the system
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicTask();
    }
    private void startPeriodicTask() {
        periodicTask = new Runnable() {
            @Override
            public void run() {
                System.out.println("here");
                new API().execute(); // Execute your AsyncTask here
                handler.postDelayed(this, INTERVAL_MILLIS); // Schedule the task to run again after the interval
            }
        };
        handler.postDelayed(periodicTask, 1000);
    }

    public void stopPeriodicTask() {
        if (handler != null && periodicTask != null) {
            handler.removeCallbacks(periodicTask);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class API extends AsyncTask<String, Void, String> {

        private String getResponse(HttpURLConnection connection) throws IOException {
            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

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
            String url = "http://192.168.1.182:3000/";
            HttpURLConnection connection_getUsername;
            try {
                if (isSignIn) {
                    connection_getUsername = getHttpRequest(url + "getUsername/?email=" + dataModel.currentUser.email);
                    if (connection_getUsername != null) {
                        String response = getResponse(connection_getUsername);
                        JSONArray json = new JSONArray(response);
                        dataModel.currentUser.username = json.getJSONObject(0).getString("username");
                        SharedPreferences.Editor editor = credentials.edit();
                        editor.putString("username", dataModel.currentUser.username);
                        editor.commit();
                    }
                }

                HttpURLConnection connection_getAllUsers = getHttpRequest(url + "getAllUsers/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_getRequests = getHttpRequest(url + "getRequests/?creator=" + dataModel.currentUser.username);
                HttpURLConnection connection_getUser = getHttpRequest(url + "getUser/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_getMeetings = getHttpRequest(url + "getMeetings/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_getGroups = getHttpRequest(url + "getGroups/?creator=" + dataModel.currentUser.username);
                HttpURLConnection connection_createdMeetings = getHttpRequest(url + "getCreatedMeetings/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_createdGroups = getHttpRequest(url + "getCreatedGroups/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_acceptedMeetings = getHttpRequest(url + "getAcceptedMeetings/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_getPreferredSports = getHttpRequest(url + "getPreferredSports/?username=" + dataModel.currentUser.username);
                HttpURLConnection connection_getJoinedGroups = getHttpRequest(url + "getJoinedGroups/?username=" + dataModel.currentUser.username);

                if (connection_getUser != null) {
                    String response = getResponse(connection_getUser);
                    JSONArray json = new JSONArray(response);
                    dataModel.currentUser.name = json.getJSONObject(0).getString("name");
                    dataModel.currentUser.email = json.getJSONObject(0).getString("email");
                    dataModel.currentUser.age = json.getJSONObject(0).getInt("age");
                    dataModel.currentUser.bio = json.getJSONObject(0).getString("bio");
                }
                if (connection_getAllUsers != null) {
                    String response = getResponse(connection_getAllUsers);
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        dataModel.users.add(new User(json.getJSONObject(i).getString("name"),
                                json.getJSONObject(i).getString("email"), json.getJSONObject(i).getString("username"),
                                json.getJSONObject(i).getInt("age"), json.getJSONObject(i).getString("bio")));
                    }
                }
                if (connection_getMeetings != null) {
                    String response = getResponse(connection_getMeetings);
                    JSONArray json = new JSONArray(response);
                    int j = 0;
                    for (int i = 0; i < json.length(); i++) {
                        while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username,
                                json.getJSONObject(i).getString("creator"))) {
                            j++;
                        }
                        dataModel.individualMeetings.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"),
                                json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"),
                                json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"),
                                dataModel.users.get(j), null));
                        j = 0;
                    }
                }
                if (connection_getRequests != null) {
                    String response = getResponse(connection_getRequests);
                    JSONArray json = new JSONArray(response);
                    int j = 0;
                    for (int i = 0; i < json.length(); i++) {
                        while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username,
                                json.getJSONObject(i).getString("username"))) {
                            j++;
                        }
                        dataModel.currentUser.requests.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"),
                                json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"),
                                json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"),
                                dataModel.currentUser, dataModel.users.get(j)));
                        if (json.getJSONObject(i).getInt("seen") == 0)
                            dataModel.currentUser.unseenRequests++;
                        j = 0;
                    }
                }
                if (connection_getGroups != null) {
                    String response = getResponse(connection_getGroups);
                    JSONArray json = new JSONArray(response);
                    int j = 0;
                    for (int i = 0; i < json.length(); i++) {
                        while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username,
                                json.getJSONObject(i).getString("creator"))) {
                            j++;
                        }
                        dataModel.groupMeetings.add(new GroupMeeting(json.getJSONObject(i).getInt("ID"),
                                json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"),
                                json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("gDate"),
                                dataModel.users.get(j), new ArrayList<>(), json.getJSONObject(i).getString("name")));
                        j = 0;
                    }
                }
                if (connection_createdMeetings != null) {
                    String response = getResponse(connection_createdMeetings);
                    JSONArray json = new JSONArray(response);
                    int j = 0;
                    for (int i = 0; i < json.length(); i++) {
                        while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username,
                                json.getJSONObject(i).getString("partner"))) {
                            j++;
                        }
                        dataModel.currentUser.createdMeetings.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"),
                                json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"),
                                json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"),
                                dataModel.currentUser, j < dataModel.users.size() ? dataModel.users.get(j) : null));
                        j = 0;
                    }

                }
                if (connection_createdGroups != null) {
                    String response = getResponse(connection_createdGroups);
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        dataModel.currentUser.createdGroups.add(new GroupMeeting(json.getJSONObject(i).getInt("ID"),
                                json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"),
                                json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("gDate"),
                                dataModel.currentUser, new ArrayList<>(), json.getJSONObject(i).getString("name")));
                    }

                }
                if (connection_acceptedMeetings != null) {
                    String response = getResponse(connection_acceptedMeetings);
                    JSONArray json = new JSONArray(response);
                    int j = 0;
                    for (int i = 0; i < json.length(); i++) {
                        while (j < dataModel.users.size() && !Objects.equals(dataModel.users.get(j).username,
                                json.getJSONObject(i).getString("creator"))) {
                            j++;
                        }
                        dataModel.currentUser.currentMatches.add(new IndividualMeeting(json.getJSONObject(i).getInt("ID"),
                                json.getJSONObject(i).getString("sport"), json.getJSONObject(i).getString("startTime"),
                                json.getJSONObject(i).getString("endTime"), json.getJSONObject(i).getString("mDate"),
                                dataModel.users.get(j),
                                json.getJSONObject(i).isNull("partner") ? null : dataModel.currentUser));
                        j = 0;
                    }
                }
                if (connection_getPreferredSports != null) {
                    String response = getResponse(connection_getPreferredSports);
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        dataModel.currentUser.preferredSports.add(json.getJSONObject(i).getString("sport"));
                    }
                }
                if (connection_getJoinedGroups != null) {
                    String response = getResponse(connection_getJoinedGroups);
                    System.out.println(response);
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        int j = 0;
                        while (j < dataModel.groupMeetings.size()
                                && dataModel.groupMeetings.get(j).ID != json.getJSONObject(i).getInt("ID"))
                            j++;
                        if (j != dataModel.groupMeetings.size()) {
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
            if (isStarting) {
                isStarting = false;
                Intent broadcastIntent = new Intent("PERIODIC_TASK_COMPLETE");
                sendBroadcast(broadcastIntent);
            }
        }

        public interface OnStart extends Serializable {
            void onTaskComplete();
        }
    }
}

