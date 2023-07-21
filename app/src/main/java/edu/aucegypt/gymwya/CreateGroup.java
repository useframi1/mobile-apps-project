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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, PostCreateGroup.MyCallback {
    DataManager dataManager = DataManager.getInstance();
    String apiResponse;
    Data dataModel = dataManager.getDataModel();
    Uri selectedImage;
    private String selectedSport;
    Button btnDatePicker, btnTimePickerFrom, btnTimePickerTo, btnCreateGroup, uploadImage;
    ImageView back;
    EditText groupName;
    static EditText groupMembersEditText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;
    SpinnerAdapter adapter;
    Spinner spinner;
    String groupNameString = "";
    String date = "";
    String fromTime = "";
    String toTime = "";
    String sportName = "";
    String gDate;

    TextView errorMessage;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    ArrayList<String> sports = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int REQUEST_PICK_IMAGE = 1;
    private StorageReference storageReference;

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
        uploadImage = (Button) findViewById(R.id.choose_icon);
        btnCreateGroup = findViewById(R.id.add_group);
        groupName = findViewById(R.id.group_name);
        groupMembersEditText = findViewById(R.id.num_of_players);
        back = findViewById(R.id.back);
        errorMessage = findViewById(R.id.error_message);

        btnDatePicker.setOnClickListener(this);
        btnTimePickerFrom.setOnClickListener(this);
        btnTimePickerTo.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(v -> {
            groupNameString = groupName.getText().toString();
            String sport = selectedSport;

            try {
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
                        JSONObject postData = new JSONObject();

                        //check if image was uploaded
                        if (selectedImage != null) {
                            try {
                                uploadImage(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //check if group icon was chosen



                        postData.put("creator", dataModel.currentUser.username);
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
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        back.setOnClickListener(this);
        uploadImage.setOnClickListener(this);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        sports.add("Sports:");
        sports.add("Football");
        sports.add("Volleyball");
        sports.add("Basketball");
        sports.add("Swimming");

        spinner = findViewById(R.id.sport);
        spinner.setOnItemSelectedListener(this);

        adapter = new SpinnerAdapter(this, sports);
        spinner.setAdapter(adapter);
        int offsetInPixels = getResources().getDimensionPixelSize(R.dimen.dropdown_offset_sports);
        spinner.setDropDownVerticalOffset(offsetInPixels);
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
            if (dataModel.previousIsHome)
                i = new Intent(this, HomePage.class);
            else
                i = new Intent(this, GroupMatching.class);
            startActivity(i);
        }
        if (v == uploadImage) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            // testing toast
            Toast.makeText(getApplicationContext(), "group Picture Set ", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean hasCollidingMeeting() {
        final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
        try {
            Date startTime = TIME_FORMAT.parse(fromTime);
            Date endTime = TIME_FORMAT.parse(toTime);

            for (int i = 0; i < dataModel.groupMeetings.size(); i++) {
                if (dataModel.groupMeetings.get(i).sport.equalsIgnoreCase(sportName)
                        && Objects.equals(dataModel.groupMeetings.get(i).date, date)) {
                    Date existingStartTime = TIME_FORMAT.parse(dataModel.groupMeetings.get(i).start);
                    Date existingEndTime = TIME_FORMAT.parse(dataModel.groupMeetings.get(i).end);

                    if ((startTime.equals(existingStartTime) && endTime.equals(existingEndTime)) ||
                            (startTime.before(existingEndTime) && endTime.after(existingStartTime)) ||
                            (startTime.before(existingStartTime) && endTime.after(existingStartTime)) ||
                            (startTime.before(existingEndTime) && endTime.after(existingEndTime)) ||
                            (startTime.after(existingStartTime) && endTime.before(existingEndTime))) {
                        return true;
                    }
                }
            }

            for (int i = 0; i < dataModel.currentUser.createdGroups.size(); i++) {
                if (dataModel.currentUser.createdGroups.get(i).sport.equalsIgnoreCase(sportName)
                        && Objects.equals(dataModel.currentUser.createdGroups.get(i).date, date)) {
                    Date existingStartTime = TIME_FORMAT.parse(dataModel.currentUser.createdGroups.get(i).start);
                    Date existingEndTime = TIME_FORMAT.parse(dataModel.currentUser.createdGroups.get(i).end);

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
                    String monthStr = (monthOfYear + 1 < 10) ? "0" + (monthOfYear + 1)
                            : String.valueOf(monthOfYear + 1);
                    String dayStr = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                    date = year + "-" + monthStr + "-" + dayStr;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        btnDatePicker.setText(
                                dayOfMonth + "-" + new DateFormatSymbols().getMonths()[monthOfYear].toString());
                        // format the date to be in the format YYYY-MM-DD
                        date = year + "-" + ((monthOfYear < 10) ? "0" : "") + (monthOfYear + 1) + "-"
                                + ((dayOfMonth < 10) ? "0" : "") + dayOfMonth;
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
                        fromTime = hourOfDay + ":" + ((minute < 10) ? "0" : "") + minute + ":00";
                    } else {
                        toTime = hourOfDay + ":" + ((minute < 10) ? "0" : "") + minute + ":00";
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void onTaskComplete(String jsonData) {
        PostAddMembers postAddMembers = new PostAddMembers("http://192.168.1.182:3000/addGroupMembers", jsonData);
        postAddMembers.execute();
    }

    private void uploadImage(Uri imageUri) throws IOException {
        if (imageUri != null) {
            // Create a unique filename for the image

            String filename = UUID.randomUUID().toString();
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageReference.child(filename);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        Task<Uri> downloadUrlTask = imageRef.getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Do something with the image URL, e.g., save it to a user profile
                            // Add your desired code here to handle the image URL

                            // Example: Add the image URL to Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("image", imageUrl);

                            db.collection("Images")
                                    .document(groupNameString) // Use userName as the document ID
                                    .set(user)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getApplicationContext(), "Image uploaded successfully",
                                                Toast.LENGTH_SHORT).show();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Failed to upload image",
                                                Toast.LENGTH_SHORT).show();
                                        // Handle failure, if needed
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
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
            members.put("ID", result);
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