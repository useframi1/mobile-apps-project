package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ViewGroup extends AppCompatActivity implements View.OnClickListener {
    CustomAdapterMembers adapter;
    ArrayList<User> members = new ArrayList<>();
    TextView groupName;
    ImageView back;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_group);

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

        Bundle bundle = getIntent().getExtras();
        String name = "Group Name";
        if (bundle!=null) {
            name = bundle.getString("Team");
            members = (ArrayList<User>) bundle.getSerializable("Members");
        }

        adapter = new CustomAdapterMembers(this, members);

        listView = findViewById(R.id.members_list);
        groupName = findViewById(R.id.group_name);
        back = findViewById(R.id.back);

        groupName.setText(name);

        back.setOnClickListener(this);

        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            finish();
        }
    }
}
class CustomAdapterMembers extends ArrayAdapter<User> {
    public CustomAdapterMembers(Context context, ArrayList<User> members) {
        super(context, 0, members);
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        User member = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(
                    getContext()).inflate(R.layout.group_members_list, parent, false);
        }

        TextView memberName = convertView.findViewById(R.id.name);
        ImageView memberPic = convertView.findViewById(R.id.profile_picture);

        memberName.setText(member.name);
        memberPic.setImageResource(member.imageId);

        return convertView;
    }
}
