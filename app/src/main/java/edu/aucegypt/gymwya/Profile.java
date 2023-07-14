package edu.aucegypt.gymwya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Profile extends AppCompatActivity {

    String [] profileItems={"People Matched","Interested Fields"};
    ListView listview;
    Button editProfile;
    EditText reminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editProfile = (Button) findViewById(R.id.editprofile);
        reminder= (EditText) findViewById(R.id.reminder);
        listview=(ListView) findViewById(R.id.profilelist);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,profileItems);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("CUSTOM_LIST_VIEW", "Item is clicked @ Position :: " +i);
                if(i==0) {
                   // Intent intent = new Intent(Profile.this, Activity_people_matched.class);
                    //startActivity(intent);
                }
                else if (i==1)
                {
                    // Intent intent = new Intent(Profile.this, Activity_interested_fields.class);
                    //startActivity(intent);
                }
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Profile.this, EditProfile.class);
                startActivity(intent);
            }
        });
        //not sure about this line yet
        String editTextValue = reminder.getText().toString();


    }
}