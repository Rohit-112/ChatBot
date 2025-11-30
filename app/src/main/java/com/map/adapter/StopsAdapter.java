package com.map.adapter;

import android.location.Location;
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
    private Location currentLocation;

    public StopsAdapter(List<Stop> stops) {
        this.stops = stops;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
        notifyDataSetChanged();
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

        if (currentLocation != null) {
            float[] dist = new float[1];

            Location.distanceBetween(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    stop.lat,
                    stop.lng,
                    dist
            );

            holder.distance.setText(formatDistance(dist[0]));
        } else {
            holder.distance.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.stopName);
            type = itemView.findViewById(R.id.stopType);
            distance = itemView.findViewById(R.id.stopDistance);
        }
    }

    public void updateStops(List<Stop> newStops) {
        this.stops = newStops;
        notifyDataSetChanged();
    }

    private String formatDistance(float meters) {
        if (meters < 1000) {
            return ((int) meters) + " m";
        } else {
            return String.format("%.2f km", meters / 1000f);
        }
    }
}
