package com.crewmates.autolibodb.api;

import com.crewmates.autolibodb.model.StateResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface api {


    @POST("createVS")
    Call<StateResponse> createVs(@Query("chassisNumber") String chassisNumber);


}
