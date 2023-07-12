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
    private List<Sport> sportList;
    private List<Sport> filteredSportList;
    private LayoutInflater inflater;
    private int convertViewLayoutResource;


    public GridViewAdapter(Context context, List<Sport> sportList) {
        this.context = context;
        this.sportList = sportList;
        this.filteredSportList = new ArrayList<>(sportList);
        inflater = LayoutInflater.from(context);
        this.convertViewLayoutResource = R.layout.grid_items;
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

    public void setConvertViewLayoutResource(int layoutResource) {
        this.convertViewLayoutResource = layoutResource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(convertViewLayoutResource, parent, false);            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.sportImage);
            holder.text = convertView.findViewById(R.id.sportText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Sport sport = filteredSportList.get(position);
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
        for (Sport sport : sportList) {
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
