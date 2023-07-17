package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class GroupMeeting extends Meeting implements Serializable {
    ArrayList<User> members = new ArrayList<>();
    public GroupMeeting(int ID, String sport, String start, String end, String date, User creator, ArrayList<User> members) {
        super(ID, sport, start, end, date, creator);
        this.members = members;
    }
}
