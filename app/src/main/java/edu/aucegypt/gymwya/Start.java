package edu.aucegypt.gymwya;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import com.google.firebase.auth.FirebaseAuth;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Start extends AppCompatActivity implements PeriodicAsyncTask.API.OnStart {
    Button signUp, signIn;

    private BroadcastReceiver taskCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isStarting = intent.getBooleanExtra("isStarting", true);
            if (isStarting)
                onTaskComplete();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataManager dataManager = DataManager.getInstance();
        Data dataModel = dataManager.getDataModel();

        SharedPreferences credentials = getSharedPreferences("Credentials", 0);
//         SharedPreferences.Editor editor = credentials.edit();
//         editor.remove("username");
//         editor.remove("email");
//         editor.remove("password");
//         editor.commit();

        System.out.println(
                credentials.contains("email") || credentials.contains("password") || credentials.contains("username"));
        if (credentials.contains("email") && credentials.contains("password") && credentials.contains("username")) {
            dataModel.currentUser.username = credentials.getString("username", "");
            dataModel.currentUser.email = credentials.getString("email", "");
            IntentFilter filter = new IntentFilter("PERIODIC_TASK_COMPLETE");
            registerReceiver(taskCompleteReceiver, filter);
            Intent serviceIntent = new Intent(this, PeriodicAsyncTask.class);
            serviceIntent.putExtra("isSignIn", false);
            startService(serviceIntent);

        } else {
            setContentView(R.layout.start);
            signUp = (Button) findViewById(R.id.signUp);
            signIn = (Button) findViewById(R.id.signIn);

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Start.this, SignIn.class);
                    startActivity(intent);
                }
            });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Start.this, SignUp.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(taskCompleteReceiver);
    }

    @Override
    public void onTaskComplete() {
        Intent i = new Intent(this, HomePage.class);
        startActivity(i);
    }
}
