package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class IndividualMeeting extends Meeting {
    User partner;
    public IndividualMeeting(int ID, String sport, String start, String end, String date, User creator, User partner) {
        super(ID, sport, start, end, date, creator);
        this.partner = partner;
    }
}
