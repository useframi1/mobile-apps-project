package edu.aucegypt.gymwya;

import java.util.ArrayList;

public class Data {
    User currentUser = new User();
    ArrayList<User> users = new ArrayList<>();
    ArrayList<IndividualMeeting> individualMeetings = new ArrayList<>();
    ArrayList<GroupMeeting> groupMeetings = new ArrayList<>();
    boolean previousIsHome;
}
