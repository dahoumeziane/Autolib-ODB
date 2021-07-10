package com.crewmates.autolibodb.api

import com.crewmates.autolibodb.model.RentalBillVehicle
import com.crewmates.autolibodb.model.RentalInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RentalByUserIdApi {
   @GET("getRental/{idUser} ")
        suspend fun getRental(
           @Path("idUser")idUser : Int
        ): Response<RentalBillVehicle>

    /*@GET("getRental ")
    suspend fun getRental(
        @Query("idUser")idUser : Int
    ): Response<RentalBillVehicle>*/
   /* @GET("getRentalInfo")
    suspend fun getRentalInfo(
        @Query("chassisNumber") chassisNumber: String
    ): RentalInfo*/
    }


