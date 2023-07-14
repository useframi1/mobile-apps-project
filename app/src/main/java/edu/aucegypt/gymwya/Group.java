package edu.aucegypt.gymwya;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
    String name;
    int iconId;
    int numberOfMembers;
    ArrayList<User> members;
    String timeFrom;
    String timeTo;
    long groupID;

    public Group(String name, int iconId, int numberOfMembers, ArrayList<User> members, String timeFrom, String timeTo) {
        this.name = name;
        this.iconId = iconId;
        this.numberOfMembers = numberOfMembers;
        this.members = members;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public Group(){

    }
}
