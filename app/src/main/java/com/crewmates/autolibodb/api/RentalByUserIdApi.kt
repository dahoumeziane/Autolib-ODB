package com.crewmates.autolibodb.api

import com.crewmates.autolibodb.model.RentalBillVehicle
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RentalByUserIdApi {
    @GET("getRental/{idUser} ")
        suspend fun getRental(
           @Path("idUser")idUser : Int
        ): RentalBillVehicle
    }


