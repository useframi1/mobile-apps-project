package edu.aucegypt.gymwya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Start extends AppCompatActivity implements API.OnStart{
    Button signUp, signIn;

    @Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataManager dataManager = DataManager.getInstance();
        Data dataModel = dataManager.getDataModel();

        SharedPreferences credentials = getSharedPreferences("Credentials", 0);
//        SharedPreferences.Editor editor = credentials.edit();
//        editor.remove("username");
//        editor.remove("email");
//        editor.remove("password");
//        editor.commit();

        System.out.println(credentials.contains("email") || credentials.contains("password") || credentials.contains("username"));
        if (credentials.contains("email") && credentials.contains("password") && credentials.contains("username")) {
            dataModel.currentUser.username = credentials.getString("username", "");
            dataModel.currentUser.email = credentials.getString("email", "");
            API api = new API(Start.this);
            api.execute("http://192.168.1.182:3000/");
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
    public void onTaskComplete() {
        Intent i = new Intent(this, HomePage.class);
        startActivity(i);
    }
}
