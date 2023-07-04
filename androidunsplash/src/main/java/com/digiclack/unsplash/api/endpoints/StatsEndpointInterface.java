package com.digiclack.unsplash.api.endpoints;

import com.digiclack.unsplash.models.Stats;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatsEndpointInterface {

    @GET("stats/total")
    Call<Stats> getStats();

}
