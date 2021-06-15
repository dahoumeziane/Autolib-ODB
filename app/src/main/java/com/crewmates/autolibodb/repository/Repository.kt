package com.crewmates.autolibodb.repository

import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.utils.RetrofitInstance
import retrofit2.Response


class Repository {


    suspend fun addPosition(location : Location) : Response<Location> {
        return RetrofitInstance.locationApi.addPosition(location)
    }
}