package edu.aucegypt.gymwya;

import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class Meeting implements Serializable {
    int ID;
    String sport;
    String start;
    String end;
    String date;
    User creator;

    public Meeting(int ID, String sport, String start, String end, String date, User creator) {
        this.ID = ID;
        this.sport = sport;
        this.start = start;
        this.end = end;
        this.date = date;
        this.creator = creator;
    }
}
