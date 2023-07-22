package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements PeriodicAsyncTask.API.OnStart {
    PeriodicAsyncTask api;
    Button signIn;
    EditText email, password;
    ImageView back;
    FirebaseAuth firebaseAuth;
    DataManager dataManager = DataManager.getInstance();
    Data dataModel = dataManager.getDataModel();

    private BroadcastReceiver taskCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isStarting = intent.getBooleanExtra("isStarting", true);
            if (isStarting)
                onTaskComplete();
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        signIn = findViewById(R.id.signInPageButton);
        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.enteredPassword);
        back = findViewById(R.id.back);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                // Validate user input
                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter both email and password", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Sign in with Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences credentials = getSharedPreferences("Credentials", 0);
                                    SharedPreferences.Editor editor = credentials.edit();
                                    editor.putString("email", userEmail);
                                    editor.putString("password", userPassword);
                                    editor.commit();
                                    dataModel.currentUser.email = userEmail;
                                    dataModel.currentUser.password = userPassword;
                                    IntentFilter filter = new IntentFilter("PERIODIC_TASK_COMPLETE");
                                    registerReceiver(taskCompleteReceiver, filter);
                                    Intent serviceIntent = new Intent(SignIn.this, PeriodicAsyncTask.class);
                                    serviceIntent.putExtra("isSignIn", true);
                                    startService(serviceIntent);
                                } else {
                                    // Sign-in failed, display an error message
                                    Toast.makeText(getApplicationContext(),
                                            "Failed to sign in. Please check your credentials.", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(taskCompleteReceiver);
    }

    @Override
    public void onTaskComplete() {
        Toast.makeText(getApplicationContext(), "Signed in successfully",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, HomePage.class);
        startActivity(i);
    }
}
