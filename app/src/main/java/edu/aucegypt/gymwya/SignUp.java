package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class SignUp extends AppCompatActivity {
    Button createAccount;
    EditText email, password, rePassword;
    ImageView back;
    ProgressDialog progressDialog;
    Timer timer;
    FirebaseAuth.AuthStateListener authStateListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        FirebaseApp.initializeApp(this);

        createAccount = (Button) findViewById(R.id.create_account);
        email = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.enteredPassword);
        rePassword = (EditText) findViewById(R.id.reEnteredPassword);
        back = findViewById(R.id.back);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        timer = new Timer(); // Create the Timer instance

        createAccount.setOnClickListener(view -> {
            if (email.getText().toString().isEmpty() && password.getText().toString().isEmpty()
                    && rePassword.getText().toString().isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (email.getText().toString().isEmpty()) {
                Toast.makeText(SignUp.this, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().isEmpty()) {
                Toast.makeText(SignUp.this, "Please enter password", Toast.LENGTH_SHORT).show();
            } else if (rePassword.getText().toString().isEmpty()) {
                Toast.makeText(SignUp.this, "Please re-enter password", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().length() < 6) {
                Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = ProgressDialog.show(SignUp.this, "", "Creating User...", true);

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();

                                authStateListener = firebaseAuth -> {
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    if (currentUser != null && currentUser.isEmailVerified()) {
                                        Intent intent = new Intent(SignUp.this, CreateUser.class);
                                        //add email and password to intent
                                        intent.putExtra("email", userEmail);
                                        intent.putExtra("password", userPassword);
                                        startActivity(intent);

                                        auth.removeAuthStateListener(authStateListener);
                                        timer.cancel();
                                        timer.purge();
                                    }
                                };

                                auth.addAuthStateListener(authStateListener);

                                user.sendEmailVerification()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignUp.this, "User Created. Please check your email for verification.",
                                                    Toast.LENGTH_SHORT).show();

                                            ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                                            progressDialog.setMessage("Verifying email...");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();

                                            TimerTask timerTask = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    FirebaseUser currentUser = auth.getCurrentUser();
                                                    if (currentUser != null) {
                                                        currentUser.reload().addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                boolean emailVerified = currentUser.isEmailVerified();
                                                                if (emailVerified) {
                                                                    Intent intent = new Intent(SignUp.this, CreateUser.class);
                                                                    //add email and password to intent
                                                                    intent.putExtra("email", userEmail);
                                                                    intent.putExtra("password", userPassword);
                                                                    startActivity(intent);

                                                                    progressDialog.dismiss();
                                                                    timer.cancel();
                                                                    timer.purge();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            };

                                            timer.schedule(timerTask, 0, 2000);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        });


                            } else {
                                progressDialog.dismiss();
                                String errorCode = ((com.google.firebase.auth.FirebaseAuthException) task.getException()).getErrorCode();
                                Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }
}
