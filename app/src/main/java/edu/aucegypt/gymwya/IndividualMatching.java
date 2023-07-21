package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Objects;

public class IndividualMatching extends AppCompatActivity {
    int i = 0;
    TextView name, bio;
    Button check, addMeeting, reject, viewProfile;
    ImageView backArrow, profilePic;
    private DataManager dataManager;
    private Data dataModel;
    String selectedSport;
    ArrayList<IndividualMeeting> meetings = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_matching);

        dataManager = DataManager.getInstance();
        dataModel = dataManager.getDataModel();
        dataModel.previousIsHome = false;

        Bundle bundle = getIntent().getExtras();
        selectedSport = bundle.getString("selectedSport");

        for (int j = 0; j < dataModel.individualMeetings.size(); j++) {
            if (Objects.equals(dataModel.individualMeetings.get(j).sport.toLowerCase(), selectedSport.toLowerCase()))
                meetings.add(dataModel.individualMeetings.get(j));
        }

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

        check = (Button) findViewById(R.id.check_button);
        backArrow = (ImageView) findViewById(R.id.back_arrow);
        profilePic = (ImageView) findViewById(R.id.profile_picture);
        addMeeting = (Button) findViewById(R.id.plus_button);
        reject = (Button) findViewById(R.id.x_button);
        viewProfile = (Button) findViewById(R.id.view_profile_button);
        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);

        if (meetings.size() == 0) {
            check.setVisibility(View.GONE);
            profilePic.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            bio.setVisibility(View.GONE);
        } else {
            check.setVisibility(View.VISIBLE);
            profilePic.setVisibility(View.VISIBLE);
            reject.setVisibility(View.VISIBLE);
            viewProfile.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            bio.setVisibility(View.VISIBLE);
            bio.setText("Wants a partner to join them at the " + meetings.get(i).sport + " from "  + meetings.get(i).start + " to "  + meetings.get(i).end +" PM");
            // profilePic.setImageResource(users.get(i).imageId);
            name.setText(meetings.get(i).creator.name);

            check.setOnClickListener(view -> match_dialog());
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i++;
                    if (i == meetings.size())
                        i=0;
//                profilePic.setImageResource(users.get(i).imageId);
                    IndividualMeeting nextMeeting = meetings.get(i);
                    name.setText(nextMeeting.creator.name);
                    bio.setText("Wants a partner to join them at the " +nextMeeting.sport + " from "  + nextMeeting.start + " to "  + nextMeeting.end +" PM");
                }
            });
            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(IndividualMatching.this, VisitProfile.class);
                    intent.putExtra("User", meetings.get(i).creator);
                    startActivity(intent);
                }
            });
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IndividualMatching.this, HomePage.class);
                startActivity(i);
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
        IndividualMeeting meeting = meetings.get(i);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);

        dialogTitle.setText("Match with " + meeting.creator.name);
        dialogMessage.setText("Are you sure you want to match with " + meeting.creator.name + "?");
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
