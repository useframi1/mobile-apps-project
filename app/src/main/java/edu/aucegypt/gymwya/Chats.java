package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Chats extends AppCompatActivity {

    ViewRequestAdapter adapter;
    ArrayList<User> senders = new ArrayList<>();


    // TextView groupName;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);


        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.home) {
                i = new Intent(this, HomePage.class);
            } else if (item.getItemId() == R.id.chats) {
                return false;
            } else {
                i = new Intent(this, CreateMeeting.class);
            }
            startActivity(i);
            return true;
        });

//        Bundle bundle = getIntent().getExtras();
//        String name = "Group Name";
//        if (bundle != null) {
//            name = bundle.getString("Team");
//            senders = (ArrayList<User>) bundle.getSerializable("Members");
//        }

        senders.add(new User("mariam", R.drawable.mariam));
        senders.add(new User("youssef", R.drawable.ghaleb));
        senders.add(new User("nadine", R.drawable.nadine));
        senders.add(new User("nour", R.drawable.nour));



        adapter = new ViewRequestAdapter(this, senders);

        listView = findViewById(R.id.request_list);

        listView.setAdapter(adapter);

//        listView.setOnItemClickListener((adapterView, view, position, l) -> {
//
//                Intent intent = new Intent(ViewRequests.this, VisitProfile.class);
//                //   intent.putExtra("User", clickedUser);
//                startActivity(intent);
//
//        });



    }


    class ViewRequestAdapter extends ArrayAdapter<User> {

        public ViewRequestAdapter(Context context, ArrayList<User> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User person = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.chat_list, parent, false);
            }

            TextView senderName = convertView.findViewById(R.id.person_name);
            TextView senderMessage = convertView.findViewById(R.id.person_message);
            ImageView senderPic = convertView.findViewById(R.id.person_image);


            senderName.setText(person.name);
            //  matchInfo.setText(sport from 3:00 PM - 4:00 PM");
            senderPic.setImageResource(person.imageId);



            return convertView;
        }
    }
}