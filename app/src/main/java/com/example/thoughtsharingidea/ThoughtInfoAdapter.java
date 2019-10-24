package com.example.thoughtsharingidea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ThoughtInfoAdapter extends ArrayAdapter<String> {

    ArrayList<String> thoughts;
    public ThoughtInfoAdapter(Context context, ArrayList<String> thougths) {
        super(context, 0, thougths);
        this.thoughts = thougths;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_list_item, parent, false);
        }
        // Lookup view for data population
        /*TextView tvName =  convertView.findViewById(R.id.replaced_layout);*/
        // Populate the data into the template view using the data object
       /* tvName.setText(thoughts.get(position));*/

        // Return the completed view to render on screen
        return convertView;
    }
}
