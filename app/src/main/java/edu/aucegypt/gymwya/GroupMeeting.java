package edu.aucegypt.gymwya;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class GroupMeeting extends Meeting implements Serializable{
    ArrayList<User> members;
    String name;
    boolean currentUserJoined;
    public GroupMeeting(int ID, String sport, String start, String end, String date, User creator, ArrayList<User> members, String name) {
        super(ID, sport, start, end, date, creator);
        this.members = members;
        this.name = name;
        currentUserJoined = false;
    }

    public GroupMeeting() {

    }
}
