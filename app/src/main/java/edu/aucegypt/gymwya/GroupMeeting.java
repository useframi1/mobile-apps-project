package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class GroupMeeting implements Serializable {
    Group group;
    Sport sport;
    User user;
    Time from, to;
    Date date;
    long meetingID;
}
