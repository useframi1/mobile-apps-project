package edu.aucegypt.gymwya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity{

    private GridViewAdapter gridAdapter;
    private List<ModelClass> sportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        BottomNavigationView menuView = findViewById(R.id.bottomNavigationView);
        menuView.setOnItemSelectedListener(item -> {
            Intent i = new Intent();
            if (item.getItemId() == R.id.home) {
                return false;
            } else if (item.getItemId() == R.id.chats) {
                i = new Intent(this, CreateGroup.class);
            } else if (item.getItemId() == R.id.profile){
                i = new Intent(this, CreateMeeting.class);
            }
            startActivity(i);
            return false;
        });

        Button viewRequest = findViewById(R.id.viewRequest);
        Spinner create = findViewById(R.id.spinner);
        SearchView searchView = findViewById(R.id.searchView);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.add("");
        spinnerAdapter.add("Create Group");
        spinnerAdapter.add("Create Individual Meeting");
        create.setAdapter(spinnerAdapter);


        create.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                if (position == 1) {
                    i = new Intent(HomePage.this, CreateGroup.class);
                    startActivity(i);
                }
                else if(position == 2) {
                    i = new Intent(HomePage.this, CreateMeeting.class);
                    startActivity(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up the GridView
        GridView gridView = findViewById(R.id.gridView);
        sportList = createSportList();
        gridAdapter = new GridViewAdapter(this, sportList);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener((adapterView, view, position, id) -> {

            Intent intent;
            if (sportList.get(position).getIsIndividual())
                intent = new Intent(HomePage.this,IndividualMatching.class);
            else
                intent = new Intent(HomePage.this,GroupMatching.class);
            intent.putExtra("selectedSport", sportList.get(position).getSportName());
            startActivity(intent);
        });

        // Set up the search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                gridAdapter.filterData(query);
                return false;
            }
        });

        viewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open another activity
            }
        });
    }

    // Create the list of sports
    private List<ModelClass> createSportList() {
        List<ModelClass> sportList = new ArrayList<>();
        sportList.add(new ModelClass("Swimming", R.drawable.swimming, false));
        sportList.add(new ModelClass("Gym", R.drawable.gym, true));
        sportList.add(new ModelClass("Football", R.drawable.football, false));
        sportList.add(new ModelClass("PingPong", R.drawable.pingpong, true));
        sportList.add(new ModelClass("BasketBall", R.drawable.basketball, false));
        sportList.add(new ModelClass("Squash", R.drawable.squash, true));
        sportList.add(new ModelClass("VolleyBall", R.drawable.volleyball, false));
        sportList.add(new ModelClass("Tennis", R.drawable.tennis, true));
        return sportList;
    }

    public static class ModelClass {
        private String sportName;
        private int sportImage;

        private boolean isIndividual;

        public ModelClass(String sportName, int sportImage, boolean isIndividual) {
            this.sportName = sportName;
            this.sportImage = sportImage;
            this.isIndividual = isIndividual;
        }

        public String getSportName() {
            return sportName;
        }

        public int getSportImage() {
            return sportImage;
        }

        public boolean getIsIndividual() {return isIndividual;}
    }
}

class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<HomePage.ModelClass> sportList;
    private List<HomePage.ModelClass> filteredSportList;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, List<HomePage.ModelClass> sportList) {
        this.context = context;
        this.sportList = sportList;
        this.filteredSportList = new ArrayList<>(sportList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredSportList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredSportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_items, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.sportImage);
            holder.text = convertView.findViewById(R.id.sportText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HomePage.ModelClass sport = filteredSportList.get(position);
        holder.image.setImageResource(sport.getSportImage());
        holder.text.setText(sport.getSportName());

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView text;
    }

    public void filterData(String query) {
        filteredSportList = new ArrayList<>();

        // Perform filtering based on the search query
        for (HomePage.ModelClass sport : sportList) {
            if (sport.getSportName().toLowerCase().contains(query.toLowerCase())) {
                filteredSportList.add(sport);
            }
        }

        // Reset the filtered list when the query is empty
        if (query.isEmpty()) {
            filteredSportList = new ArrayList<>(sportList);
        }

        notifyDataSetChanged();
    }
}

