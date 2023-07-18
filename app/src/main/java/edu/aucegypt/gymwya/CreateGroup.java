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
import java.util.Calendar;
import java.util.List;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();
    private String selectedSport,date,fromTime,toTime;
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, btnCreateGroup;
    ImageView back;
    EditText groupName;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;
//    private DataManager dataManager;
//    private Data dataModel;

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

        btnDatePicker=(Button)findViewById(R.id.date);
        btnTimePickerFrom=(Button)findViewById(R.id.from);
        btnTimePickerTo=(Button)findViewById(R.id.to);
        btnCreateGroup = findViewById(R.id.add_group);
        groupName = findViewById(R.id.group_name);
        back = findViewById(R.id.back);

        btnDatePicker.setOnClickListener(this);
        btnTimePickerFrom.setOnClickListener(this);
        btnTimePickerTo.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNameString = groupName.getText().toString();
                String sport = selectedSport;
                String startTime = btnTimePickerFrom.getText().toString();
                String endTime = btnTimePickerTo.getText().toString();
                //String date = btnDatePicker.getText().toString();
               // String admin = adminEditText.getText().toString();

                try {
                    // Create a JSON object with the user-entered data
                    JSONObject postData = new JSONObject();
                    postData.put("creator", "ahhhhhhh");
                    postData.put("name", groupNameString);
                    postData.put("sport", sport);
                    postData.put("startTime", fromTime);
                    postData.put("endTime", toTime);
                    postData.put("gDate", date);

                    // Convert the JSONObject to a string
                    String jsonString = postData.toString();

                    // Replace the URL with your actual Node.js API endpoint
                    String url = "http://192.168.56.1:3000/createGroup";

                    // Execute the POST request asynchronously using the PostCreateGroup AsyncTask
                    PostCreateGroup asyncTask = new PostCreateGroup(url, jsonString);
                    asyncTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        } if (v == btnTimePickerFrom) {
            setTime(btnTimePickerFrom,true);
        } if (v == btnTimePickerTo) {
            setTime(btnTimePickerTo,false);
        } if (v == back) {
            Intent i;
            if (dataModel.previousIsHome)
                i = new Intent(this, HomePage.class);
            else i = new Intent(this, GroupMatching.class);
            startActivity(i);
        }
    }

//    public void setDate() {
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                (view, year, monthOfYear, dayOfMonth) -> {
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
//                    }
//
//                }, mYear, mMonth, mDay);
//        datePickerDialog.show();
//    }

    public void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String monthStr = (monthOfYear + 1 < 10) ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                    String dayStr = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                    // Format the date as 'YYYY-MM-DD'
                    date = year + "-" + monthStr + "-" + dayStr;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
                    }

                   // btnDatePicker.setText(date);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


//    public void setTime(Button button) {
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                (view, hourOfDay, minute) -> {
//                    int tempHour = hourOfDay;
//                    if (hourOfDay == 0)
//                        tempHour+=12;
//                    else if (hourOfDay>12)
//                        tempHour-=12;
//                    button.setText(tempHour + ":" + ((minute<10)? "0" : "") + minute  + " " + ((hourOfDay < 12) ? "AM" : "PM"));
//                }, mHour, mMinute,false);
//        timePickerDialog.show();
//    }

    public void setTime(Button button, boolean isStart) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    //String amPm = (hourOfDay < 12) ? "AM" : "PM";
                    String hour = (hourOfDay == 0 || hourOfDay == 12) ? "12" : String.valueOf(hourOfDay % 12);
                    String minuteStr = (minute < 10) ? "0" + minute : String.valueOf(minute);
                    button.setText(hour + ":" + minuteStr + " " + ((hourOfDay < 12) ? "AM" : "PM"));

                    // Format the time as 'HH:mm:ss'
                    if (isStart)
                        fromTime = hour + ":" + minuteStr + ":00 ";
                    else
                        toTime =  hour + ":" + minuteStr + ":00";
                    //button.setText(time);
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    private class PostCreateGroup extends AsyncTask<String, Void, Void> {

        private String jsonData;
        private String url;
        public PostCreateGroup(String url,String jsonData) {
            this.jsonData = jsonData;
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... params) {
            // String urlString = params[0];
            String result = "";
            HttpURLConnection connection;
            try {
                // String urlStr = "http://192.168.56.1/createGroup";
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
    }

}

