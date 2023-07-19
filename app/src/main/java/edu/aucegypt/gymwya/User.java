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

    public User(String name, String email, String username, int age, String bio) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.age = age;
        this.bio = bio;
    }

    public User() {

    }
}

