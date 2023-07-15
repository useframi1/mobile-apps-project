package edu.aucegypt.gymwya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateUser extends AppCompatActivity {
    private GridViewAdapter gridAdapter;
    private List<Sport.SportIcon> iconsList = new ArrayList<>();
    Button create;
    ImageView back;
    private IconsAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        create = findViewById(R.id.createUserButton);
        back = findViewById(R.id.back);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recyclerView);
        iconsList.add(new Sport.SportIcon(R.drawable.football_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.volleyball_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.tennis_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.squash_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.basketball_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.swimming_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.pingpong_icon));
        iconsList.add(new Sport.SportIcon(R.drawable.gym_icon));

        mAdapter = new IconsAdapter(iconsList, false, true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        create.setOnClickListener(v -> {
            Intent intent = new Intent(CreateUser.this, HomePage.class);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            finish();
        });

        //retrieve data from the previous activity
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        //retrieve the user name from the username text field in the xml file
        String userName = ((TextView) findViewById(R.id.username)).getText().toString();
        //checK if user name is taken and print a toast if it is
        /*
        DocumentReference documentRef = db.collection("users").document(userName);
        documentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // The document with the specified ID exists
                    // Handle the case when the document exists
                    Toast.makeText(getApplicationContext(), "username exists", Toast.LENGTH_SHORT).show();
                } else {
                    // The document with the specified ID does not exist
                    // Handle the case when the document does not exist
                    Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                // An error occurred while checking for the document
                // Handle the error
                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        db.collection("user")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateUser.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                        // Continue with your logic after data insertion
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateUser.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                        // Handle failure, if needed
                    }
                });*/
    }
}