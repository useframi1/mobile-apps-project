package edu.aucegypt.gymwya;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String name;
    String email;
    String username;
    String password;
    int age;
    int pic;
    String bio;
    int unseenRequests = 0;
    ArrayList<IndividualMeeting> requests = new ArrayList<>();
    ArrayList<IndividualMeeting> currentMatches = new ArrayList<>();
    ArrayList<GroupMeeting> joinedGroups = new ArrayList<>();
    ArrayList<IndividualMeeting> createdMeetings = new ArrayList<>();
    ArrayList<GroupMeeting> createdGroups = new ArrayList<>();
    ArrayList<String> preferredSports = new ArrayList<>();

    public User(String name, String email, String username, int age, String bio) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.age = age;
        this.bio = bio;
    }

    public User(String name, int pic) {
        this.name = name;
        this.pic = pic;

    }
    public User() {

    }

    public ArrayList<Sport.SportIcon> getPreferredSportsIcons() {
        ArrayList<Sport.SportIcon> preferredSportsIcons = new ArrayList<>();

        for (int i = 0; i < preferredSports.size(); i++) {
            switch (preferredSports.get(i).toLowerCase()) {
                case "basketball" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.basketball_icon));
                case "tennis" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.tennis_icon));
                case "football" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.football_icon));
                case "gym" -> preferredSportsIcons.add(new Sport.SportIcon(R.drawable.gym_icon));
                case "ping pong" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.pingpong_icon));
                case "squash" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.squash_icon));
                case "swimming" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.swimming_icon));
                case "volleyball" ->
                        preferredSportsIcons.add(new Sport.SportIcon(R.drawable.volleyball_icon));
            }
        }

        for (int i = 0; i < preferredSportsIcons.size(); i++) {
            System.out.println(preferredSportsIcons.get(i).id);
        }
        return preferredSportsIcons;
    }
}

