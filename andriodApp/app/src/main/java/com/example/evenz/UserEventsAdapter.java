package com.example.evenz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserEventsAdapter extends ArrayAdapter<String> {

    public UserEventsAdapter(Context context, ArrayList<String> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.event_string, parent, false);
        } else {
            view = convertView;
        }
        String event = super.getItem(position);
        TextView eventView = view.findViewById(R.id.event);

        eventView.setText(event);
        return view;
    }
}
