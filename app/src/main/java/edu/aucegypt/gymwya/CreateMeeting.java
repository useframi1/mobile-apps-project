package edu.aucegypt.gymwya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormatSymbols;
import java.util.Calendar;

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
            if (dataModel.previousIsHome)
                i = new Intent(this, HomePage.class);
            else i = new Intent(this, IndividualMatching.class);
            startActivity(i);
        } if (v == addMeeting) {
        }
    }

    public void setDate() {
        datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
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
                }, mHour, mMinute,false);
        timePickerDialog.show();
    }
}