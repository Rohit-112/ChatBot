package com.map.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.map.R;
import com.map.model.Stop;

import java.util.List;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ViewHolder> {

    private List<Stop> stops;

    public StopsAdapter(List<Stop> stops) {
        this.stops = stops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stop, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stop stop = stops.get(position);
        holder.name.setText(stop.name);
        holder.type.setText(stop.type.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.stopName);
            type = itemView.findViewById(R.id.stopType);
        }
    }
}

