package com.crewmates.autolibodb.api
import com.crewmates.autolibodb.model.StateResponse
import com.crewmates.autolibodb.model.Task
import com.crewmates.autolibodb.model.VehicleState
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TechDetailsUpdateApi {

    @POST("AddTechDetails")
    suspend fun updateVehicleState(
        @Body state : VehicleState
    ): Response<VehicleState>

    @GET("getVehicleInformations?chassisNumber=AF1LM2")
    suspend fun getState():VehicleState

    @POST("service-task/task")
    suspend fun alertOilChange(
        @Body state : Task
    ): Response<Task>


    @POST("createVS")
    suspend fun createVs(
        @Query("chassisNumber") chassisNumber: String
    ): Response<StateResponse>
}