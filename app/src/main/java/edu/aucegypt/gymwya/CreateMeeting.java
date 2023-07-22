package edu.aucegypt.gymwya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateMeeting extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, addMeeting, uploadImage;
    ImageView back;
    TextView errorMessage;
    private static final int REQUEST_PICK_IMAGE = 1;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SpinnerAdapter adapter;
    Spinner spinner;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private DataManager dataManager;
    private Data dataModel;
    String date = "";
    String fromTime = "";
    String toTime = "";
    String sportName = "";
    ArrayList<String> sports = new ArrayList<>();

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

        btnDatePicker = findViewById(R.id.date);
        btnTimePickerFrom = findViewById(R.id.from);
        btnTimePickerTo = findViewById(R.id.to);
        addMeeting = findViewById(R.id.add_meeting);
        errorMessage = findViewById(R.id.error_message);

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

        sports.add("Sports:");
        sports.add("Tennis");
        sports.add("Ping pong");
        sports.add("Gym");
        sports.add("Squash");

        spinner = findViewById(R.id.sport);
        spinner.setOnItemSelectedListener(this);

        adapter = new SpinnerAdapter(this, sports);
        spinner.setAdapter(adapter);
        int offsetInPixels = getResources().getDimensionPixelSize(R.dimen.dropdown_offset_sports);
        spinner.setDropDownVerticalOffset(offsetInPixels);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sportName = sports.get(position);
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
            setTime(btnTimePickerFrom);
        }
        if (v == btnTimePickerTo) {
            setTime(btnTimePickerTo);
        }
        if (v == back) {
            Intent i;
            if (dataModel.previousIsHome) {
                i = new Intent(this, HomePage.class);
            } else {
                i = new Intent(this, IndividualMatching.class);
            }
            startActivity(i);
        }
        if (v == addMeeting) {
            if (!Objects.equals(fromTime, "") && !Objects.equals(toTime, "") && !Objects.equals(date, "")
                    && !Objects.equals(sportName, "")) {
                if (isInvalidMeeting()) {
                    errorMessage.setText("Please choose valid time");
                    errorMessage.setVisibility(View.VISIBLE);
                } else if (hasCollidingMeeting()) {
                    errorMessage.setText("There is another meeting at that time");
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    errorMessage.setVisibility(View.INVISIBLE);
                    CreateUserTask createUserTask = new CreateUserTask();
                    createUserTask.execute(dataModel.currentUser.username, sportName, fromTime, toTime, date);
                }
            }
        }
        if (v == uploadImage) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 0);
        }
    }

    public boolean hasCollidingMeeting() {
        final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
        try {
            String temp = date.substring(0, date.length() - 1) + (date.charAt(date.length() - 1) - 49);
            Date startTime = TIME_FORMAT.parse(fromTime);
            Date endTime = TIME_FORMAT.parse(toTime);

            for (int i = 0; i < dataModel.individualMeetings.size(); i++) {
                System.out.println(
                        dataModel.individualMeetings.get(i).sport.toLowerCase() + " " + sportName.toLowerCase());
                System.out.println(dataModel.individualMeetings.get(i).date + " " + date);
                if (dataModel.individualMeetings.get(i).sport.equalsIgnoreCase(sportName)
                        && Objects.equals(dataModel.individualMeetings.get(i).date.substring(0, 10), temp)) {
                    Date existingStartTime = TIME_FORMAT.parse(dataModel.individualMeetings.get(i).start);
                    Date existingEndTime = TIME_FORMAT.parse(dataModel.individualMeetings.get(i).end);

                    if ((startTime.equals(existingStartTime) && endTime.equals(existingEndTime)) ||
                            (startTime.before(existingEndTime) && endTime.after(existingStartTime)) ||
                            (startTime.before(existingStartTime) && endTime.after(existingStartTime)) ||
                            (startTime.before(existingEndTime) && endTime.after(existingEndTime)) ||
                            (startTime.after(existingStartTime) && endTime.before(existingEndTime))) {
                        return true;
                    }
                }
            }

            for (int i = 0; i < dataModel.currentUser.createdMeetings.size(); i++) {
                if (dataModel.currentUser.createdMeetings.get(i).sport.equalsIgnoreCase(sportName)
                        && Objects.equals(dataModel.currentUser.createdMeetings.get(i).date.substring(0, 10), temp)) {
                    Date existingStartTime = TIME_FORMAT.parse(dataModel.currentUser.createdMeetings.get(i).start);
                    Date existingEndTime = TIME_FORMAT.parse(dataModel.currentUser.createdMeetings.get(i).end);

                    if ((startTime.equals(existingStartTime) && endTime.equals(existingEndTime)) ||
                            (startTime.before(existingEndTime) && endTime.after(existingStartTime)) ||
                            (startTime.before(existingStartTime) && endTime.after(existingStartTime)) ||
                            (startTime.before(existingEndTime) && endTime.after(existingEndTime)) ||
                            (startTime.after(existingStartTime) && endTime.before(existingEndTime))) {
                        return true;
                    }
                }
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean isInvalidMeeting() {
        final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTimeStr = date + " " + fromTime;
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (dateTime.isBefore(currentDateTime))
                return true;
        }

        try {
            Date startTime = TIME_FORMAT.parse(fromTime);
            Date endTime = TIME_FORMAT.parse(toTime);

            if (startTime.after(endTime)) {
                return true;
            } else
                return false;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDate() {
        datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(
                                dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
                        // format the date to be in the format YYYY-MM-DD
                        date = year + "-" + ((monthOfYear < 10) ? "0" : "") + (monthOfYear + 1) + "-"
                                + ((dayOfMonth < 10) ? "0" : "") + (dayOfMonth);
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTime(Button button) {
        timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    int tempHour = hourOfDay;
                    if (hourOfDay == 0)
                        tempHour += 12;
                    else if (hourOfDay > 12)
                        tempHour -= 12;
                    button.setText(tempHour + ":" + ((minute < 10) ? "0" : "") + minute + " "
                            + ((hourOfDay < 12) ? "AM" : "PM"));
                    if (button == btnTimePickerFrom) {
                        // add the time to from fromTime in the format HH:MM:SS 24 hour format
                        fromTime = (hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + ((minute < 10) ? "0" : "") + minute
                                + ":00";
                    } else {
                        toTime = (hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + ((minute < 10) ? "0" : "") + minute
                                + ":00";
                    }
                }, mHour, mMinute, false);
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
                        .url("http://192.168.1.182:3000/createMeeting")
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
            if (result != null) {
                Toast.makeText(getApplicationContext(), "meeting created successfully", Toast.LENGTH_SHORT).show();
                dataModel.currentUser.createdMeetings.add(new IndividualMeeting(Integer.parseInt(result), sportName,
                        fromTime, toTime, date, dataModel.currentUser, null));
            } else {
                Toast.makeText(getApplicationContext(), "Failed to create meeting", Toast.LENGTH_SHORT).show();
            }
        }
    }

}