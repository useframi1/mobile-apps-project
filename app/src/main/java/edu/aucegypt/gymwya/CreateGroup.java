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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, btnCreateGroup;
    ImageView back;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;
    private DataManager dataManager;
    private Data dataModel;
    String creator;
    String name;
    String sport;
    String startTime;
    String endTime;
    String gDate;


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
        back = findViewById(R.id.back);

        btnDatePicker.setOnClickListener(this);
        btnTimePickerFrom.setOnClickListener(this);
        btnTimePickerTo.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(this);
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
            if (dataModel.previousIsHome)
                i = new Intent(this, HomePage.class);
            else i = new Intent(this, GroupMatching.class);
            startActivity(i);
        } if (v == btnCreateGroup) {
            CreateGroup.CreateUserTask createUserTask = new CreateGroup.CreateUserTask();
            sport = ((Spinner)findViewById(R.id.sport)).getSelectedItem().toString();
            name = ((EditText)findViewById(R.id.group_name)).getText().toString();
            createUserTask.execute("AUCAUC", name, sport, startTime, endTime, gDate);
        }
    }

    public void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
                        gDate = year + "-" + ((monthOfYear<10)? "0" : "") + (monthOfYear+1) + "-" + ((dayOfMonth<10)? "0" : "") + dayOfMonth;
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTime(Button button) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    int tempHour = hourOfDay;
                    if (hourOfDay == 0)
                        tempHour+=12;
                    else if (hourOfDay>12)
                        tempHour-=12;
                    button.setText(tempHour + ":" + ((minute<10)? "0" : "") + minute  + " " + ((hourOfDay < 12) ? "AM" : "PM"));
                    if (button == btnTimePickerFrom) {
                        //add the time to from fromTime in the format HH:MM:SS 24 hour format
                        startTime = hourOfDay + ":" + ((minute<10)? "0" : "") + minute  + ":00";
                    } else {
                        endTime = hourOfDay + ":" + ((minute<10)? "0" : "") + minute  + ":00";
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
                // add the following to the jsonBody (creator, name, sport, startTime, endTime, gDate)
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("creator", params[0]);
                jsonBody.put("name", params[1]);
                jsonBody.put("sport", params[2]);
                jsonBody.put("startTime", params[3]);
                jsonBody.put("endTime", params[4]);
                jsonBody.put("gDate", params[5]);

                // Create an HTTP request
                okhttp3.Request request = new Request.Builder()
                        .url("http://192.168.56.1:3000/createGroup")
                        .post(RequestBody.create(JSON, jsonBody.toString()))
                        .build();

                // Send the request and get the response
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
                // User created successfully
                Toast.makeText(getApplicationContext(), "meeting created successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreateGroup.this, HomePage.class);
                startActivity(intent);
            } else {
                // Failed to create user
                Toast.makeText(getApplicationContext(), "Failed to create meeting", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

