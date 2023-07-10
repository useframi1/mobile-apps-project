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

public class IndividualMatching extends AppCompatActivity {
    int i =0;

    TextView name, bio;
    Button check, addMeeting, reject, viewProfile;
    ImageView backArrow, profilePic,home,chat,profile;;
    String[] users = {"Jennifer Lopez","Nour", "Youssef", "Mariam", "Nadine", "Dana", "Omar"};
    int [] profiles = {R.drawable.barbary,R.drawable.nour, R.drawable.ghaleb, R.drawable.mariam, R.drawable.nadine, R.drawable.dana};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_matching);
        check = (Button) findViewById(R.id.check_button);
        backArrow = (ImageView) findViewById(R.id.back_arrow);
        profilePic = (ImageView) findViewById(R.id.profile_picture);
        addMeeting = (Button) findViewById(R.id.plus_button);
        reject = (Button) findViewById(R.id.x_button);
        viewProfile = (Button) findViewById(R.id.view_profile_button);
        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);
        home = (ImageView) findViewById(R.id.home_icon);
        chat = (ImageView) findViewById(R.id.messages_icon);
        profile = (ImageView) findViewById(R.id.profile_icon);

        Bundle bundle = getIntent().getExtras();
        String sport = bundle.getString("selectedSport");
//        String startTime = bundle.getString("start");
//        String endTime = bundle.getString("end");

        //inital profile
        profilePic.setImageResource(profiles[i]);
        name.setText(users[i]);
        bio.setText("Wants a partner to join them at the " + sport + " from 2:30 to 4:00 PM");

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match_dialog();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i == profiles.length)
                    i=0;

                profilePic.setImageResource(profiles[i]);
                name.setText(users[i]);
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

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualMatching.this, HomePage.class);
                startActivity(intent);
            }
        });

        //    chat.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(GroupMatching.this, ChatPage.class);
//            startActivity(intent);
//        }
//    });

        //    profile.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(GroupMatching.this, userProfile.class);
//            startActivity(intent);
//        }
//    });
    }

    private void match_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IndividualMatching.this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);

        dialogTitle.setText("Matched!");
        dialogMessage.setText("You matched with " + users[i] + "!");
        cancelButton.setText("Cancel");
        confirmButton.setText("Confirm");

        AlertDialog dialog = builder.create();

      cancelButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              dialog.dismiss();
          }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualMatching.this,GroupMatching.class);
                startActivity(intent);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
