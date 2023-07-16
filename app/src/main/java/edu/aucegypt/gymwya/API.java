package edu.aucegypt.gymwya;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class API extends Worker {
    private static final String TAG = "MyWorker";
    private DataManager dataManager;

    public API(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataManager = DataManager.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Data dataModel = dataManager.getDataModel();
            Log.e(TAG, "here");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error occurred: " + e.getMessage());
            return Result.failure();
        }
    }
}
