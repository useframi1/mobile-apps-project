package edu.aucegypt.gymwya;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.MyViewHolder> {
    private List<Sport.SportIcon> iconsList;
    private ArrayList<MyViewHolder> iconsView = new ArrayList<>();
    private boolean toggle;
    private boolean isEditable;

    public Object getSelectedItems() {
        ArrayList<Sport.SportIcon> selectedItems = new ArrayList<>();
        for (int i = 0; i < iconsList.size(); i++) {
            if (iconsList.get(i).isPressed)
                selectedItems.add(iconsList.get(i));
        }
        return selectedItems;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        boolean isPressed;
        View view;

        MyViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.group_icon);
            view.setBackgroundResource(0);
            isPressed = false;
            this.view = view;
        }
    }

    public IconsAdapter(List<Sport.SportIcon> iconsList, boolean toggle, boolean isEditable) {
        this.iconsList = iconsList;
        this.toggle = toggle;
        this.isEditable = isEditable;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icons_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        iconsView.add(myViewHolder);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        int icon = iconsList.get(position).id;
        holder.icon.setImageResource(icon);
        if (isEditable)
            holder.itemView.setOnClickListener(v -> {
                for (int i = 0; i < iconsView.size(); i++) {
                    if (iconsView.get(i).view != v) {
                        if (toggle) {
                            iconsView.get(i).view.setBackgroundResource(0);
                            iconsList.get(i).isPressed = false;
                            iconsView.get(i).isPressed = false;
                        }
                    }
                }
                iconsView.get(position).view.setBackgroundResource(holder.isPressed ? 0 : R.drawable.circle_bg);
                iconsList.get(position).isPressed = !holder.isPressed;
                iconsView.get(position).isPressed = !holder.isPressed;
            });
    }

    @Override
    public int getItemCount() {
        return iconsList.size();
    }
}
