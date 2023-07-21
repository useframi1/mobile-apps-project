package edu.aucegypt.gymwya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> menuTexts;

    public SpinnerAdapter(Context context, ArrayList<String> menuTexts) {
        this.context = context;
        this.menuTexts = menuTexts;
    }

    @Override
    public int getCount() {
        return menuTexts != null ? menuTexts.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false);
        TextView txt = rootView.findViewById(R.id.text);

        txt.setText(menuTexts.get(position));

        return rootView;
    }
}
