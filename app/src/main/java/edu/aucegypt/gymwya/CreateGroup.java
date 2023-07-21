package edu.aucegypt.gymwya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, PostCreateGroup.MyCallback {
    DataManager dataManager = DataManager.getInstance();
    String apiResponse;
    Data dataModel = dataManager.getDataModel();
    private String selectedSport, date, fromTime, toTime;
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, btnCreateGroup;
    ImageView back;
    EditText groupName;
    static EditText groupMembersEditText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        dataManager = DataManager.getInstance();
        dataModel = dataManager.getDataModel();

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

        recyclerView = findViewById(R.id.recyclerView);
        iconsList.add(new Sport.SportIcon(R.drawable.football_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.volleyball_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.tennis_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.squash_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.basketball_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.swimming_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.pingpong_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.gym_icon));

        mAdapter = new IconsAdapter(iconsList, true, true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        btnDatePicker = (Button) findViewById(R.id.date);
        btnTimePickerFrom = (Button) findViewById(R.id.from);
        btnTimePickerTo = (Button) findViewById(R.id.to);
        btnCreateGroup = findViewById(R.id.add_group);
        groupName = findViewById(R.id.group_name);
        groupMembersEditText = findViewById(R.id.num_of_players);
        back = findViewById(R.id.back);

        btnDatePicker.setOnClickListener(this);
        btnTimePickerFrom.setOnClickListener(this);
        btnTimePickerTo.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNameString = groupName.getText().toString();
                String sport = selectedSport;

                String members = CreateGroup.groupMembersEditText.getText().toString();
                String[] membersList = members.split(",");

                try {
                    JSONObject postData = new JSONObject();

                    postData.put("creator","sawsan");
                    postData.put("name", groupNameString);
                    postData.put("sport", sport);
                    postData.put("startTime", btnTimePickerFrom.getText().toString());
                    postData.put("endTime", btnTimePickerTo.getText().toString());
                    postData.put("gDate", btnDatePicker.getText().toString());

                    String jsonString = postData.toString();

                    String url = "http://192.168.56.1:3000/createGroup";
                    System.out.println("create group");
                    PostCreateGroup asyncTask = new PostCreateGroup(url, jsonString, CreateGroup.this);
                    asyncTask.execute();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(CreateGroup.this, ViewCreatedMeetings.class);
               startActivity(i);
            }


        });
        back.setOnClickListener(this);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.sport);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSport = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {
            setDate();
        }
        if (v == btnTimePickerFrom) {
            setTime(btnTimePickerFrom, true);
        }
        if (v == btnTimePickerTo) {
            setTime(btnTimePickerTo, false);
        }
        if (v == back) {
            Intent i;
            if (dataModel.previousIsHome)
                i = new Intent(this, HomePage.class);
            else i = new Intent(this, GroupMatching.class);
            startActivity(i);
        }
    }


    public void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String monthStr = (monthOfYear + 1 < 10) ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                    String dayStr = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                    date = year + "-" + monthStr + "-" + dayStr;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTime(Button button, boolean isStart) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String hour = (hourOfDay == 0 || hourOfDay == 12) ? "12" : String.valueOf(hourOfDay % 12);
                    String minuteStr = (minute < 10) ? "0" + minute : String.valueOf(minute);
                    button.setText(hour + ":" + minuteStr + " " + ((hourOfDay < 12) ? "AM" : "PM"));

                    if (isStart)
                        fromTime = hour + ":" + minuteStr + ":00 ";
                    else
                        toTime = hour + ":" + minuteStr + ":00";
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void onTaskComplete(String jsonData) {
        System.out.println("in on task complete");
        System.out.println(jsonData);
        PostAddMembers addMembers = new PostAddMembers("http://192.168.56.1:3000/addGroupMembers", jsonData);
        addMembers.execute();
    }
}

class PostCreateGroup extends AsyncTask<String, Void, String> {

    private String jsonData;
    private String url;
    MyCallback myCallback;

    public PostCreateGroup(String url, String jsonData, MyCallback myCallback) {
        System.out.println("here");
        this.jsonData = jsonData;
        this.url = url;
        this.myCallback = myCallback;
    }

    @Override
    protected String doInBackground(String... params) {
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
                System.out.println("heere");
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                System.out.println(response);
                result = response.toString();
                System.out.println("result:");
                System.out.println(result);
                return result;
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
    protected void onPostExecute(String result) {

        JSONObject members = new JSONObject();

        String groupMembersString = CreateGroup.groupMembersEditText.getText().toString();
        String[] membersArray = groupMembersString.split(",");

        ArrayList<String> groupMembersArray = new ArrayList<>(Arrays.asList(membersArray));
        JSONArray groupMembersJSON = new JSONArray(groupMembersArray);


        try {
            members.put("ID", result );
            members.put("groupMembers", groupMembersJSON);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonString2 = members.toString();
        if (groupMembersJSON.length() > 0)
             myCallback.onTaskComplete(jsonString2);
    }

    public interface MyCallback {
        void onTaskComplete(String jsonData);
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
}

