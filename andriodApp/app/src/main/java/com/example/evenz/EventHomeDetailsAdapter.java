package com.example.evenz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventHomeDetailsAdapter extends RecyclerView.Adapter<EventHomeDetailsAdapter.ViewHolder> {

    private final List<String> eventDetails;

    public EventHomeDetailsAdapter(List<String> eventDetails) {
        this.eventDetails = eventDetails;
    }

    @Override
    public EventHomeDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHomeDetailsAdapter.ViewHolder holder, int position) {
        String detail = eventDetails.get(position);
        holder.txtEventDetail.setText(detail);
    }

    @Override
    public int getItemCount() {
        return eventDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEventDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            txtEventDetail = itemView.findViewById(R.id.text_event_location);
        }
    }
}
