package edu.aucegypt.gymwya;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    int imageId;
    public User(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }
}

