package edu.aucegypt.gymwya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Start extends AppCompatActivity {
    Button signUp, signIn;

    @Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences credentials = getSharedPreferences("Credentials", 0);
//        SharedPreferences.Editor editor = credentials.edit();
//        editor.putString("username", "mariam");
//        editor.commit();
//        if (credentials.contains("username")) {
//            Intent i = new Intent(this, HomePage.class);
//            startActivity(i);
//        }

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
