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

public class ViewCreatedGroups extends AppCompatActivity {

    ViewGroupsCreatedAdapter adapter;
    ImageView back;
    ListView listView;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_created_groups);


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

        ArrayList<GroupMeeting> allGroups = dataModel.currentUser.createdGroups;
        allGroups.addAll(dataModel.currentUser.joinedGroups);

        adapter = new ViewGroupsCreatedAdapter(this, allGroups);

        listView = findViewById(R.id.view_created_groups_list);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(this, edu.aucegypt.gymwya.ViewGroup.class);
            intent.putExtra("Group", dataModel.currentUser.createdGroups.get(position));
            startActivity(intent);
        });
    }


    class ViewGroupsCreatedAdapter extends ArrayAdapter<GroupMeeting> {

        public ViewGroupsCreatedAdapter(Context context, ArrayList<GroupMeeting> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupMeeting meeting = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.created_groups_list, parent, false);
            }

            TextView groupName = convertView.findViewById(R.id.group_name);
            TextView sport = convertView.findViewById(R.id.sport);
            TextView from = convertView.findViewById(R.id.from);
            TextView to = convertView.findViewById(R.id.to);
            TextView date = convertView.findViewById(R.id.date);

            groupName.setText(meeting.name);
            String firstChar = meeting.sport.substring(0, 1).toUpperCase();
            String restOfString = meeting.sport.substring(1).toLowerCase();
            sport.setText(firstChar+restOfString);
            from.setText(meeting.start);
            to.setText(meeting.end);
            date.setText(meeting.date.substring(0,10));

            return convertView;
        }
    }
}
