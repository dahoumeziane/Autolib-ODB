package com.crewmates.autolibodb.api
import com.crewmates.autolibodb.model.Location
import com.crewmates.autolibodb.model.VehicleState
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TechDetailsUpdateApi {

    @POST("AddTechDetails")
    suspend fun updateVehicleState(
        @Body state : VehicleState
    ): Response<VehicleState>

    @GET("getVehicleInformations?chassisNumber=AF1LM2")
    suspend fun getState():VehicleState
}