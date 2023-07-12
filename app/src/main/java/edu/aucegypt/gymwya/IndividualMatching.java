package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class IndividualMatching extends AppCompatActivity {
    int i = 0;

    TextView name, bio;
    Button check, addMeeting, reject, viewProfile;
    ImageView backArrow, profilePic;

    ArrayList<User> users = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_matching);

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

        users.add(new User("Jennifer Lopez", R.drawable.barbary));
        users.add(new User("Nour", R.drawable.nour));
        users.add(new User("Youssef", R.drawable.ghaleb));
        users.add(new User("Mariam", R.drawable.mariam));
        users.add(new User("Nadine", R.drawable.nadine));
        users.add(new User("Dana", R.drawable.dana));


        check = (Button) findViewById(R.id.check_button);
        backArrow = (ImageView) findViewById(R.id.back_arrow);
        profilePic = (ImageView) findViewById(R.id.profile_picture);
        addMeeting = (Button) findViewById(R.id.plus_button);
        reject = (Button) findViewById(R.id.x_button);
        viewProfile = (Button) findViewById(R.id.view_profile_button);
        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);


        Bundle bundle = getIntent().getExtras();
        String sport = bundle.getString("selectedSport");
        bio.setText("Wants a partner to join them at the " + sport + " from 2:30 to 4:00 PM");
//        String startTime = bundle.getString("start");
//        String endTime = bundle.getString("end");

        //inital profile
        profilePic.setImageResource(users.get(i).imageId);
        name.setText(users.get(i).name);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        check.setOnClickListener(view -> match_dialog());
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i == users.size())
                    i=0;

                profilePic.setImageResource(users.get(i).imageId);
                name.setText(users.get(i).name);
                bio.setText("Wants a partner to join them at the " + sport + " from 2:30 to 4:00 PM");
            }
        });
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualMatching.this, GroupMatching.class);
                //Intent intent = new Intent(IndividualMatching.this, Profile.class);
                //intent.putExtra("user",users[position]);
                startActivity(intent);
            }
        });

        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualMatching.this, CreateMeeting.class);
                startActivity(intent);
            }
        });
    }

    private void match_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);

        dialogTitle.setText("Match with " + users.get(i).name);
        dialogMessage.setText("Are you sure you want to match with " + users.get(i).name + "?");
        cancelButton.setText("Cancel");
        confirmButton.setText("Confirm");

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,HomePage.class);
            startActivity(intent);
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
