package com.crewmates.autolibodb.utils


import com.crewmates.autolibodb.MainActivity
import com.crewmates.autolibodb.api.LocationUpdateApi

import com.crewmates.autolibodb.api.RentalByUserIdApi

import com.crewmates.autolibodb.api.PanneDetectionApi
import com.crewmates.autolibodb.api.TechDetailsUpdateApi
import com.crewmates.autolibodb.utils.Constants.Companion.LOCATION_BASE_URL
import com.crewmates.autolibodb.utils.Constants.Companion.RENTAL_BILL_VEHICLE_URL
import com.crewmates.autolibodb.utils.Constants.Companion.PANNE_BASE_URL
import com.crewmates.autolibodb.utils.Constants.Companion.STATE_BASE_URL
import com.crewmates.autolibodb.utils.Constants.Companion.TASK_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Logger


object RetrofitInstance {


    private val retrofitLocation by lazy {
        Retrofit.Builder()
            .baseUrl("https://5a457f1a4294.ngrok.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val locationApi : LocationUpdateApi by lazy {
        retrofitLocation.create(LocationUpdateApi::class.java)
    }

    private val retrofitState by lazy {
        Retrofit.Builder()
            .baseUrl("https://aa776763a764.ngrok.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val stateApi : TechDetailsUpdateApi by lazy {
        retrofitState.create(TechDetailsUpdateApi::class.java)
    }

    private val retrofitTask by lazy {
        Retrofit.Builder()
            .baseUrl(TASK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val taskApi : TechDetailsUpdateApi by lazy {
        retrofitTask.create(TechDetailsUpdateApi::class.java)
    }


    /*private val retrofitRental by lazy {

        Retrofit.Builder()
            .baseUrl("https://4aec2aaf36e8.ngrok.io/rentalUser/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val RentalByUserIdApi : RentalByUserIdApi by lazy {

        retrofitRental.create(RentalByUserIdApi::class.java)

    }*/


    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }


    fun retrofitInstance(url: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()






    val ByUserIdApi : RentalByUserIdApi by lazy {

        retrofitInstance(RENTAL_BILL_VEHICLE_URL).create(RentalByUserIdApi::class.java)

    }


    private val retrofitPanne by lazy {
        Retrofit.Builder()
            .baseUrl(PANNE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val panneApi : PanneDetectionApi by lazy {
        retrofitPanne.create(PanneDetectionApi::class.java)
    }
}