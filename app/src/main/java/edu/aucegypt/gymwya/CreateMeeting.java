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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateMeeting extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, addMeeting;
    ImageView back;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Sport sport;
    ArrayAdapter<CharSequence> adapter;
    Spinner spinner;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private DataManager dataManager;
    private Data dataModel;
    String date ;
    String fromTime;
    String toTime;
    String sportName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting);

        dataManager = DataManager.getInstance();
        dataModel = dataManager.getDataModel();

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

        btnDatePicker= findViewById(R.id.date);
        btnTimePickerFrom= findViewById(R.id.from);
        btnTimePickerTo= findViewById(R.id.to);
        addMeeting = findViewById(R.id.add_meeting);

        btnDatePicker.setOnClickListener(this);
        btnTimePickerFrom.setOnClickListener(this);
        btnTimePickerTo.setOnClickListener(this);
        addMeeting.setOnClickListener(this);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        adapter = ArrayAdapter.createFromResource(this, R.array.sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.sport);
        //sportName = spinner.getSelectedItem().toString();
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {
            setDate();
        } if (v == btnTimePickerFrom) {
            setTime(btnTimePickerFrom);
        } if (v == btnTimePickerTo) {
            setTime(btnTimePickerTo);
        } if (v == back) {
            Intent i;
            if (dataModel.previousIsHome) {
                i = new Intent(this, HomePage.class);
            }
            else {
                i = new Intent(this, IndividualMatching.class);
            }
           // startActivity(i);
        } if (v == addMeeting) {
            CreateUserTask createUserTask = new CreateUserTask();
            String sportName = spinner.getSelectedItem().toString();
            createUserTask.execute("feweeee", sportName, fromTime, toTime, date);

        }
    }

    public void setDate() {
        datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
                        //format the date to be in the format YYYY-MM-DD
                        date = year + "-" + ((monthOfYear<10)? "0" : "") + (monthOfYear+1) + "-" + ((dayOfMonth<10)? "0" : "") + dayOfMonth;
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTime(Button button) {
        timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    int tempHour = hourOfDay;
                    if (hourOfDay == 0)
                        tempHour+=12;
                    else if (hourOfDay>12)
                        tempHour-=12;
                    button.setText(tempHour + ":" + ((minute<10)? "0" : "") + minute  + " " + ((hourOfDay < 12) ? "AM" : "PM"));
                    if (button == btnTimePickerFrom) {
                        //add the time to from fromTime in the format HH:MM:SS 24 hour format
                        fromTime = hourOfDay + ":" + ((minute<10)? "0" : "") + minute  + ":00";
                    } else {
                        toTime = hourOfDay + ":" + ((minute<10)? "0" : "") + minute  + ":00";
                    }
                }, mHour, mMinute,false);
        timePickerDialog.show();
    }

    private class CreateUserTask extends AsyncTask<String, Void, String> {

        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("creator", params[0]);
                jsonBody.put("sport", params[1]);
                jsonBody.put("startTime", params[2]);
                jsonBody.put("endTime", params[3]);
                jsonBody.put("mDate", params[4]);


                okhttp3.Request request = new Request.Builder()
                        .url("http://192.168.56.1:3000/createMeeting")
                        .post(RequestBody.create(JSON, jsonBody.toString()))
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.equals("1")) {
                Toast.makeText(getApplicationContext(), "meeting created successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreateMeeting.this, HomePage.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to create meeting", Toast.LENGTH_SHORT).show();
            }
        }
    }


}