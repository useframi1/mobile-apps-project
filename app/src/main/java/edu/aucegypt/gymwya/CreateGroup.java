package edu.aucegypt.gymwya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, btnCreateGroup;
    ImageView back;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<Icon> iconsList = new ArrayList<>();
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.home) {
                i = new Intent(this, HomePage.class);
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, CreateGroup.class);
            } else {
                i = new Intent(this, CreateMeeting.class);
            }
            startActivity(i);
            return true;
        });

        recyclerView = findViewById(R.id.recyclerView);
        iconsList.add(new Icon(R.drawable.football, false));
        iconsList.add(new Icon(R.drawable.volleyball, false));
        iconsList.add(new Icon(R.drawable.tennis, false));
        iconsList.add(new Icon(R.drawable.squash, false));
        iconsList.add(new Icon(R.drawable.basketball, false));
        iconsList.add(new Icon(R.drawable.swimming, false));
        iconsList.add(new Icon(R.drawable.pingpong, false));
        iconsList.add(new Icon(R.drawable.gym, false));

        mAdapter = new IconsAdapter(iconsList);
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
            finish();
        } if (v == btnCreateGroup) {
            for (int i = 0; i < iconsList.size(); i++) {
                System.out.println(iconsList.get(i).id + ": " + iconsList.get(i).isPressed);
            }
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

    public class Icon {
        public int id;
        public boolean isPressed;

        public Icon(int id, boolean isPressed) {
            this.id = id;
            this.isPressed = isPressed;
        }
    }
}

class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.MyViewHolder> {
    private List<CreateGroup.Icon> iconsList;
    private ArrayList<MyViewHolder> iconsView = new ArrayList<>();
    private int chosenIcon;
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        boolean isPressed;
        View view;
        MyViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.group_icon);
            view.setBackgroundResource(0);
            isPressed = false;
            this.view = view;
        }
    }
    public IconsAdapter(List<CreateGroup.Icon> iconsList) {
        this.iconsList = iconsList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icons_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        iconsView.add(myViewHolder);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        int icon = iconsList.get(position).id;
        holder.icon.setImageResource(icon);
        holder.itemView.setOnClickListener(v -> {
            for (int i = 0; i < iconsView.size(); i++) {
                if (iconsView.get(i).view != v) {
                    iconsView.get(i).view.setBackgroundResource(0);
                    iconsList.get(i).isPressed = false;
                    iconsView.get(i).isPressed = false;
                }
            }
            iconsView.get(position).view.setBackgroundResource(holder.isPressed? 0 : R.drawable.circle_bg);
            iconsList.get(position).isPressed = !holder.isPressed;
            iconsView.get(position).isPressed = !holder.isPressed;
        });
    }
    @Override
    public int getItemCount() {
        return iconsList.size();
    }

    public int getChosenIconId() {
        return chosenIcon;
    }
}