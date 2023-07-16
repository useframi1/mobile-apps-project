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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, btnCreateGroup;
    ImageView back;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;
    private DataManager dataManager;
    private Data dataModel;

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

