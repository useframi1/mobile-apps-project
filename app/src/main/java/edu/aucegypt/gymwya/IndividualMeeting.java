package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class IndividualMeeting implements Serializable {
    Sport sport;
    User user1, user2;
    Time from, to;
    Date date;
    boolean bothMatched;
    long meetingID;
}
