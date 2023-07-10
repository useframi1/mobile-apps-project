package edu.aucegypt.gymwya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo;
    ImageView back;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        btnDatePicker=(Button)findViewById(R.id.date);
        btnTimePickerFrom=(Button)findViewById(R.id.from);
        btnTimePickerTo=(Button)findViewById(R.id.to);

        btnDatePicker.setOnClickListener(this);
        btnTimePickerFrom.setOnClickListener(this);
        btnTimePickerTo.setOnClickListener(this);

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
            finish();
        }
    }

    public void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
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
                }, mHour, mMinute,false);
        timePickerDialog.show();
    }
}