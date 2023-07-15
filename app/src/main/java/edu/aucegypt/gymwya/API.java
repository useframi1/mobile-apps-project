package edu.aucegypt.gymwya;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.Time;
import java.sql.Timestamp;

public class API extends Worker {
    private static final String TAG = "MyWorker";
    private Main dataManager;

    public API(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataManager = Main.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            SubMain dataModel = dataManager.getDataModel();
            Log.e(TAG, "here");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error occurred: " + e.getMessage());
            return Result.failure();
        }
    }
}
