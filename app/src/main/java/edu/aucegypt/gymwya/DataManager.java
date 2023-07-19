package edu.aucegypt.gymwya;

public class DataManager {
    private static DataManager instance;
    private Data dataModel;

    private DataManager() {
        dataModel = new Data();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public Data getDataModel() {
        return dataModel;
    }
}
