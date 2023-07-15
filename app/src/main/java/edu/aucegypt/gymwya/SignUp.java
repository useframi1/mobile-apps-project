package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicReference;

public class SignUp extends AppCompatActivity {
        Button createAccount;
        EditText email, password, rePassword;
        ImageView back;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.sign_up);

                createAccount = (Button) findViewById(R.id.create_account);
//                googleSignUp = (Button) findViewById(R.id.continueWithGoogle);
                email = (EditText) findViewById(R.id.userEmail);
                password = (EditText) findViewById(R.id.enteredPassword);
                rePassword = (EditText) findViewById(R.id.reEnteredPassword);
                back = findViewById(R.id.back);


                createAccount.setOnClickListener(view -> {
//                        FirebaseApp.initializeApp(this);
//                        AtomicReference<Boolean> databaseError = new AtomicReference<>();
                        Intent intent = new Intent(this, CreateUser.class);
                        startActivity(intent);
//                        add check for email and password to see if they are empty or not and if they are empty then send error message
//                        if (!password.getText().toString().equals(rePassword.getText().toString())) {
//                                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//                        } else if (password.getText().toString().length() < 6 && password.getText().toString().length() > 0) {
//                                Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
//                        }else if(email.getText().toString().isEmpty() && password.getText().toString().isEmpty() && rePassword.getText().toString().isEmpty()) {
//                                Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                        } else{
//                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
//                                        if (task.isSuccessful()) {
//                                                Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                                Toast.makeText(SignUp.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                        databaseError.set(!task.isSuccessful());
//                                });
//                                if (!databaseError.get()) {
//                                        intent.putExtra("password", password.getText().toString());
//                                        intent.putExtra("email", email.getText().toString());
//                                        startActivity(intent);
//                                }
//                        }
                });
                // googleSignUp.setOnClickListener(new View.OnClickListener() {
                // @Override
                // public void onClick(View view) {
                // Intent intent = new Intent(SignUp.this, NewAccount.class);
                // intent = intent.putExtra("email", email.getText().toString());
                // startActivity(intent);
                // }
                // });

                back.setOnClickListener(v -> {
                        finish();
                });

        }
}
