package edu.aucegypt.gymwya;

import java.sql.Date;
import java.sql.Time;

public class Meeting {
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
