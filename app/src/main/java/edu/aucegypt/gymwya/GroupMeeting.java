package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class GroupMeeting extends Meeting implements Serializable {
    ArrayList<User> members = new ArrayList<>();
}
