package edu.aucegypt.gymwya;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Main {
    private static Main instance;
    private SubMain dataModel;

    private Main() {
        dataModel = new SubMain();
    }

    public static synchronized Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public SubMain getDataModel() {
        return dataModel;
    }
}
