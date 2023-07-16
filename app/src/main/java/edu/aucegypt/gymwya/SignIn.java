package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.content.Intent;
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

public class SignIn extends AppCompatActivity {
    Button signIn;
    EditText email, password;
    ImageView back;
    FirebaseAuth firebaseAuth;

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
                    Toast.makeText(getApplicationContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sign in with Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    //toast
                                    Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                                    // Sign-in successful, proceed to the HomePage activity
                                    Intent intent = new Intent(SignIn.this, HomePage.class);
                                    intent.putExtra("email", userEmail);
                                    startActivity(intent);

                                } else {
                                    // Sign-in failed, display an error message
                                    Toast.makeText(getApplicationContext(), "Failed to sign in. Please check your credentials.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }
}