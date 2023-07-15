package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ViewRequests extends Main{

    ViewRequestAdapter adapter;
    ArrayList<User> match_list = new ArrayList<>();


    // TextView groupName;
    ImageView back;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);


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

//        Bundle bundle = getIntent().getExtras();
//        String name = "Group Name";
//        if (bundle != null) {
//            name = bundle.getString("Team");
//            match_list = (ArrayList<User>) bundle.getSerializable("Members");
//        }

        match_list.add(new User("youssef", R.drawable.ghaleb));
        match_list.add(new User("nour", R.drawable.nour));
        match_list.add(new User("youssef", R.drawable.ghaleb));
        match_list.add(new User("nour", R.drawable.nour));
        match_list.add(new User("youssef", R.drawable.ghaleb));
        match_list.add(new User("nour", R.drawable.nour));



        adapter = new ViewRequestAdapter(this, match_list);

        listView = findViewById(R.id.request_list);
        back = findViewById(R.id.back);

        //groupName.setText(name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // listView.setAdapter(adapter);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ViewRequests.this, VisitProfile.class);
            intent.putExtra("User", match_list.get(position).name);
            startActivity(intent);
        });
    }
    class ViewRequestAdapter extends ArrayAdapter<User> {
        Button decline, confirm;

        public ViewRequestAdapter(Context context, ArrayList<User> members) {
            super(context, 0, members);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User person = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.requests_list, parent, false);
            }

            TextView requestPerson = convertView.findViewById(R.id.name);
            TextView matchInfo = convertView.findViewById(R.id.info);
            ImageView personPic = convertView.findViewById(R.id.profile_picture);
            decline = convertView.findViewById(R.id.decline);
            confirm = convertView.findViewById(R.id.confirm);

            requestPerson.setText(person.name);
            //  matchInfo.setText(sport from 3:00 PM - 4:00 PM");
            personPic.setImageResource(person.imageId);

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    match_list.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            return convertView;
        }
    }


}
