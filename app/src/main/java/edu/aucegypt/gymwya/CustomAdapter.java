package edu.aucegypt.gymwya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> teamNames;
    private final ArrayList<Integer> groupPictures;
    private final ArrayList<String> teamMembers;
    private final ArrayList<Integer> memberNumber;
    private final ArrayList<String> teamTime;
    LayoutInflater inflater;

    public CustomAdapter(Context contxt, ArrayList<String> Names, ArrayList<Integer> Pictures, ArrayList<String> Members, ArrayList<Integer> Number, ArrayList<String> Time) {
        this.context = contxt;
        this.teamNames = Names;
        this.groupPictures = Pictures;
        this.teamMembers = Members;
        this.memberNumber = Number;
        this.teamTime = Time;
        inflater = LayoutInflater.from(contxt);

    }


    @Override
    public int getCount() {
        return teamNames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int totalGroupNumber;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.groups_list, parent, false);
        }

        ImageView teamImage = convertView.findViewById(R.id.team_image);
        TextView nameOfTeam = convertView.findViewById(R.id.team_name);
        TextView teamMember = convertView.findViewById(R.id.team_members);
        TextView teamNumber = convertView.findViewById(R.id.team_size);
        TextView teamSlot = convertView.findViewById(R.id.playing_time);

        teamImage.setImageResource(groupPictures.get(position));
        nameOfTeam.setText(teamNames.get(position));
       // teamMember.setText(teamMembers[position]);

        StringBuilder membersBuilder = new StringBuilder();

        int membersSize = memberNumber.get(position);
        for (int i = 0; i < membersSize; i++) {
            membersBuilder.append(teamMembers.get(i));
            if (i < membersSize - 1) {
                membersBuilder.append(", ");
            }
        }
        teamMember.setText(membersBuilder.toString());

        if (membersSize <6) {
            totalGroupNumber = 6;
        } else {
            totalGroupNumber = 12;
        }
        String group = String.valueOf(memberNumber.get(position)) + "/" + String.valueOf(totalGroupNumber);
        teamNumber.setText(String.valueOf(group));
        teamSlot.setText(teamTime.get(position));

        return convertView;
    }


}
