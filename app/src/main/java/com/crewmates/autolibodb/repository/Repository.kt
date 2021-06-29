package com.crewmates.autolibodb.repository

import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.VehicleState
import com.crewmates.autolibodb.utils.RetrofitInstance
import retrofit2.Response


class Repository {


    suspend fun addPosition(location : Location) : Response<Location> {
        return RetrofitInstance.locationApi.addPosition(location)
    }
    suspend fun updateVehicleState(state : VehicleState) : Response<VehicleState> {
        return RetrofitInstance.stateApi.updateVehicleState(state)
    }
    suspend fun getState(): VehicleState {
        return RetrofitInstance.stateApi.getState()
    }
}