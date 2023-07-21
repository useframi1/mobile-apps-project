package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Objects;

public class ViewCreatedMeetings extends AppCompatActivity {

    ViewMeetingsCreatedAdapter adapter;
    ImageView back;
    ListView listView;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_created_meetings);


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

        ArrayList<IndividualMeeting> meetings = new ArrayList<>();
        meetings.addAll(dataModel.currentUser.createdMeetings);
        meetings.addAll(dataModel.currentUser.currentMatches);
        adapter = new ViewMeetingsCreatedAdapter(this, meetings);

        listView = findViewById(R.id.view_created_meetings_list);
        back = findViewById(R.id.back);

        //groupName.setText(name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            if (!meetings.get(position).creator.username.equals(dataModel.currentUser.username)){
                Intent intent = new Intent(this, VisitProfile.class);
                intent.putExtra("User", meetings.get(position).creator);
                startActivity(intent);
            }
        });
    }


    class ViewMeetingsCreatedAdapter extends ArrayAdapter<IndividualMeeting> {

        public ViewMeetingsCreatedAdapter(Context context, ArrayList<IndividualMeeting> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IndividualMeeting meeting = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.created_meetings_list, parent, false);
            }

            TextView sport = convertView.findViewById(R.id.sport);
            TextView from = convertView.findViewById(R.id.from);
            TextView to = convertView.findViewById(R.id.to);
            TextView date = convertView.findViewById(R.id.date);
            TextView status = convertView.findViewById(R.id.status);
            TextView creator = convertView.findViewById(R.id.creator);
            ImageView arrow_btn = convertView.findViewById(R.id.arrow_button);

            if (meeting.creator.username.equals(dataModel.currentUser.username))
                arrow_btn.setVisibility(View.INVISIBLE);
            creator.setText(Objects.equals(meeting.creator.username, dataModel.currentUser.username) ?"You":meeting.creator.username);
            String firstChar = meeting.sport.substring(0, 1).toUpperCase();
            String restOfString = meeting.sport.substring(1).toLowerCase();
            sport.setText(firstChar+restOfString);
            from.setText(meeting.start);
            to.setText(meeting.end);
            date.setText(meeting.date.substring(0,10));
            if (meeting.partner == null) {
                status.setText("Pending");
                status.setTextColor(getResources().getColor(R.color.dark_grey));
            } else {
                status.setText("Matched");
                status.setTextColor(getResources().getColor(R.color.blue));
            }


            return convertView;
        }
    }
}