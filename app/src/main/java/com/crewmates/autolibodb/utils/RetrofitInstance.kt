package com.crewmates.autolibodb.utils


import com.crewmates.autolibodb.api.LocationUpdateApi
import com.crewmates.autolibodb.utils.Constants.Companion.LOCATION_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {


    private val retrofitLocation by lazy {
        Retrofit.Builder()
            .baseUrl("https://c802c2577b3b.ngrok.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val locationApi : LocationUpdateApi by lazy {
        retrofitLocation.create(LocationUpdateApi::class.java)
    }
}