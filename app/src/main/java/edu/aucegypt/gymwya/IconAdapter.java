package edu.aucegypt.gymwya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

    private List<Integer> iconList;
    private Context context;

    public IconAdapter( Context context,List<Integer> iconList) {
        this.iconList = iconList;
        this.context = context;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_sport_icons, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        int iconResId = iconList.get(position);
        holder.iconImageView.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.sport);
        }
    }
}
