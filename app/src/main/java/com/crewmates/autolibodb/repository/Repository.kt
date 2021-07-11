package com.crewmates.autolibodb.repository

import com.crewmates.autolibodb.MainActivity
import com.crewmates.autolibodb.model.*
import com.crewmates.autolibodb.utils.RetrofitInstance
import retrofit2.Response
import java.util.logging.Logger


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
    suspend fun getState(chassisNumber : String): Vehicle {
        return RetrofitInstance.stateApi.getState(chassisNumber)
    }
    suspend fun createVs(chassisNumber : String) : Response<StateResponse> {
        return RetrofitInstance.stateApi.createVs(chassisNumber)
    }
    suspend fun getRentalInfo(chassisNumber : String) : RentalInfo {
        return RetrofitInstance.stateApi.getRentalInfo(chassisNumber)
    }

    suspend fun getRentalUser(idUser: Int) :Response<RentalBillVehicle>{
        return RetrofitInstance.ByUserIdApi.getRental(idUser)
    }
    suspend fun detectPannes(panne : PannesData): Message {
        return RetrofitInstance.panneApi.detectPanne(panne)

    }
}