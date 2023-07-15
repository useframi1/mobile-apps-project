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

public class SignIn extends AppCompatActivity {
    Button signIn;
    EditText email, password;
    ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        signIn = (Button) findViewById(R.id.signInPageButton);
        email = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.enteredPassword);
        back = findViewById(R.id.back);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignIn.this, HomePage.class);
                intent = intent.putExtra("email", email.getText().toString());
                startActivity(intent);
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }
}
