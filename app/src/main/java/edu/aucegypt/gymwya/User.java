package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String name;
    String email;
    String username;
    String password;
    int age;
    String bio;
    ArrayList<IndividualMeeting> requests = new ArrayList<>();
    ArrayList<Meeting> currentMatches = new ArrayList<>();
    ArrayList<Meeting> createdMeetings = new ArrayList<>();
    ArrayList<String> preferredSports = new ArrayList<>();
}

