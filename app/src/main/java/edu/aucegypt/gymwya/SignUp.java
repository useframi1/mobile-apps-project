package edu.aucegypt.gymwya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
        Button createAcccount, googleSignUp;
        EditText email, password, rePassword;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.sign_up);

                createAcccount = (Button) findViewById(R.id.create_account);
                googleSignUp = (Button) findViewById(R.id.continueWithGoogle);
                email = (EditText) findViewById(R.id.userEmail);
                password = (EditText) findViewById(R.id.enteredPassword);
                rePassword = (EditText) findViewById(R.id.reEnteredPassword);

                createAcccount.setOnClickListener(view -> {
                        Intent intent = new Intent(SignUp.this, CreateUser.class);
                        intent = intent.putExtra("email", email.getText().toString());
                        startActivity(intent);
                });
                // googleSignUp.setOnClickListener(new View.OnClickListener() {
                // @Override
                // public void onClick(View view) {
                // Intent intent = new Intent(SignUp.this, NewAccount.class);
                // intent = intent.putExtra("email", email.getText().toString());
                // startActivity(intent);
                // }
                // });

        }
}
