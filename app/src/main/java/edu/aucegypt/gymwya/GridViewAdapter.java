package edu.aucegypt.gymwya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<ModelClass> sportList;
    private List<ModelClass> filteredSportList;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, List<ModelClass> sportList) {
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

        ModelClass sport = filteredSportList.get(position);
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
        for (ModelClass sport : sportList) {
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
