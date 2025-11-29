package com.map.network;

import com.map.model.StopsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StopApi {

    @GET("bus_stops.json")
    Call<StopsResponse> getBusStops();

    @GET("metro_stops.json")
    Call<StopsResponse> getMetroStops();
}
