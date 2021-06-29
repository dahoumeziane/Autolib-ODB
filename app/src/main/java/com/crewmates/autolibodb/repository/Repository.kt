package com.crewmates.autolibodb.repository

import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.StateResponse
import com.crewmates.autolibodb.model.Task
import com.crewmates.autolibodb.model.VehicleState
import com.crewmates.autolibodb.utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Response


class Repository {


    suspend fun addPosition(location : Location) : Response<Location> {
        return RetrofitInstance.locationApi.addPosition(location)
    }
    suspend fun updateVehicleState(state : VehicleState) : Response<VehicleState> {
        return RetrofitInstance.stateApi.updateVehicleState(state)
    }
    suspend fun alertOilChange(task : Task) : Response<Task> {
        return RetrofitInstance.taskApi.alertOilChange(task)
    }
    suspend fun getState(): VehicleState {
        return RetrofitInstance.stateApi.getState()
    }
    suspend fun createVs(chassisNumber : String) : Response<StateResponse> {
        return RetrofitInstance.stateApi.createVs(chassisNumber)
    }
}