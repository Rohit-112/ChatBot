package com.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.map.adapter.StopsAdapter;
import com.map.model.Stop;
import com.map.model.StopsResponse;
import com.map.network.StopRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private RecyclerView rvStops;
    private StopsAdapter adapter;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private final List<Stop> allStops = new ArrayList<>();
    private final List<Marker> activeMarkers = new ArrayList<>();

    private LatLng currentCameraCenter;

    private static final int LOCATION_REQ = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvStops = findViewById(R.id.rvStops);
        rvStops.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout bottomSheet = findViewById(R.id.bottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        adapter = new StopsAdapter(new ArrayList<>());
        rvStops.setAdapter(adapter);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        fetchStops();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        LatLng delhi = new LatLng(28.6139, 77.2090);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi, 13));

        setupCameraListeners();
        enableMyLocation();
    }

    private void setupCameraListeners() {
        googleMap.setOnCameraIdleListener(() -> {
            currentCameraCenter = googleMap.getCameraPosition().target;
            float zoom = googleMap.getCameraPosition().zoom;

            updateVisibleStops(zoom);
        });
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQ
            );
            return;
        }

        googleMap.setMyLocationEnabled(true);
    }

    private void fetchStops() {
        StopRepository repo = new StopRepository();

        repo.getApi().getBusStops().enqueue(new Callback<StopsResponse>() {
            @Override
            public void onResponse(Call<StopsResponse> call, Response<StopsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allStops.addAll(response.body().stops);
                }
                fetchMetroStops();
            }

            @Override
            public void onFailure(Call<StopsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed bus stops", Toast.LENGTH_SHORT).show();
                fetchMetroStops();
            }
        });
    }

    private void fetchMetroStops() {
        StopRepository repo = new StopRepository();

        repo.getApi().getMetroStops().enqueue(new Callback<StopsResponse>() {
            @Override
            public void onResponse(Call<StopsResponse> call, Response<StopsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allStops.addAll(response.body().stops);
                }
                Toast.makeText(MainActivity.this, "Stops loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<StopsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed metro stops", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVisibleStops(float zoom) {
        if (currentCameraCenter == null) return;

        List<Stop> filtered = new ArrayList<>();

        double radiusMeters;
        if (zoom >= 15) radiusMeters = 2000;
        else if (zoom >= 13) radiusMeters = 4000;
        else radiusMeters = 8000;

        for (Stop s : allStops) {
            float[] distance = new float[1];
            Location.distanceBetween(currentCameraCenter.latitude, currentCameraCenter.longitude,
                    s.lat, s.lng, distance);

            if (distance[0] <= radiusMeters) {
                filtered.add(s);
            }
        }

        updateMarkers(filtered);
        adapter.updateStops(filtered);
    }

    private void updateMarkers(List<Stop> stops) {
        for (Marker m : activeMarkers) m.remove();
        activeMarkers.clear();

        for (Stop s : stops) {
            LatLng pos = new LatLng(s.lat, s.lng);

            float icon = s.type.equalsIgnoreCase("bus")
                    ? BitmapDescriptorFactory.HUE_RED
                    : BitmapDescriptorFactory.HUE_BLUE;

            Marker marker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(pos)
                            .title(s.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(icon))
            );

            activeMarkers.add(marker);
        }
    }
}
